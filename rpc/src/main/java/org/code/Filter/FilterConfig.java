package org.code.Filter;


import lombok.Getter;
import lombok.SneakyThrows;
import org.code.spi.ExtensionLoader;

import java.io.IOException;

/**
 * The type Filter config.
 */
public class FilterConfig {

    @Getter
    private static FilterChain serviceBeforeFilterChain = new FilterChain();
    @Getter
    private static FilterChain serviceAfterFilterChain = new FilterChain();
    @Getter
    private static FilterChain clientBeforeFilterChain = new FilterChain();
    @Getter
    private static FilterChain clientAfterFilterChain = new FilterChain();

    /**
     * Init service filter.
     */
    @SneakyThrows
    public static void initServiceFilter() {
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(ServiceAfterFilter.class);
        extensionLoader.loadExtension(ServiceBeforeFilter.class);
        serviceBeforeFilterChain.addFilter(extensionLoader.gets(ServiceBeforeFilter.class));
        serviceAfterFilterChain.addFilter(extensionLoader.gets(ServiceAfterFilter.class));
    }

    /**
     * Init client filter.
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    @SneakyThrows
    public static void initClientFilter() {
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(ClientAfterFilter.class);
        extensionLoader.loadExtension(ClientBeforeFilter.class);
        clientBeforeFilterChain.addFilter(extensionLoader.gets(ClientBeforeFilter.class));
        clientAfterFilterChain.addFilter(extensionLoader.gets(ClientAfterFilter.class));
    }

}
