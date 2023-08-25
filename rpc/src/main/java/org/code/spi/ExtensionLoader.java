package org.code.spi;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Extension loader.
 */
@Slf4j
public class ExtensionLoader {

    // System SPI
    private static String SYS_EXTENSION_LOADER_DIR_PREFIX = "META-INF/xrpc/";
    // User SPI
    private static String DIY_EXTENSION_LOADER_DIR_PREFIX = "META-INF/rpc/";
    private static String[] prefixes = {SYS_EXTENSION_LOADER_DIR_PREFIX, DIY_EXTENSION_LOADER_DIR_PREFIX};
    // bean definition information key: defined key value: concrete class
    private static Map<String, Class> extensionClassCache = new ConcurrentHashMap<>();
    // bean definition information key: interface value: interface subclass s
    private static Map<String, Map<String, Class>> extensionClassCaches = new ConcurrentHashMap<>();
    // instantiated bean
    private static Map<String, Object> singletonsObject = new ConcurrentHashMap<>();
    private static ExtensionLoader extensionLoader;

    static {
        extensionLoader = new ExtensionLoader();
    }

    private ExtensionLoader() {

    }

    /**
     * Get instance extension loader.
     * @return the extension loader
     */
    public static ExtensionLoader getInstance() {
        return extensionLoader;
    }

    /**
     * get bean
     * @param <V>  the type parameter
     * @param name the name
     * @return v v
     */
    public <V> V get(String name) {
        if (!singletonsObject.containsKey(name)) {
            try {
                singletonsObject.put(name, extensionClassCache.get(name).newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (V) singletonsObject.get(name);
    }

    /**
     * Get all classes under the interface
     * @param clazz the clazz
     * @return the
     */
    public List<Object> gets(Class clazz) {

        final String name = clazz.getName();
        if (!extensionClassCaches.containsKey(name)) {
            try {
                throw new ClassNotFoundException(clazz + "not found");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        final Map<String, Class> stringClassMap = extensionClassCaches.get(name);
        List<Object> objects = new ArrayList<>();
        if (stringClassMap.size() > 0) {
            stringClassMap.forEach((k, v) -> {
                try {
                    objects.add(singletonsObject.getOrDefault(k, v.newInstance()));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

        return objects;
    }

    /**
     * According to the spi mechanism, the initial loading bean information is put into the map
     * @param clazz the clazz
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public void loadExtension(Class clazz) throws IOException, ClassNotFoundException {
        if (clazz == null) {
            throw new IllegalArgumentException("class not found");
        }
        ClassLoader classLoader = this.getClass().getClassLoader();
        Map<String, Class> classMap = new HashMap<>();
        // Find beans from system SPI and user SPI
        for (String prefix : prefixes) {
            String spiFilePath = prefix + clazz.getName();
            Enumeration<URL> enumeration = classLoader.getResources(spiFilePath);
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                InputStreamReader inputStreamReader = null;
                inputStreamReader = new InputStreamReader(url.openStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] lineArr = line.split("=");
                    String key = lineArr[0];
                    String name = lineArr[1];
                    final Class<?> aClass = Class.forName(name);
                    extensionClassCache.put(key, aClass);
                    classMap.put(key, aClass);
                    log.info("Load bean key:{}, value:{}", key, name);
                }
            }
        }
        extensionClassCaches.put(clazz.getName(), classMap);
    }

}
