package org.code.registry;


import org.code.spi.ExtensionLoader;

/**
 * The type Registry factory.
 */
public class RegistryFactory {

    /**
     * Get registry service.
     * @param registryService the registry service
     * @return the registry service
     * @throws Exception the exception
     */
    public static RegistryService get(String registryService) throws Exception {
        return ExtensionLoader.getInstance().get(registryService);
    }

    /**
     * Init.
     * @throws Exception the exception
     */
    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(RegistryService.class);
    }

}
