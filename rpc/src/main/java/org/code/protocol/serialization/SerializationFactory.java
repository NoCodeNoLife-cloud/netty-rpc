package org.code.protocol.serialization;


import org.code.spi.ExtensionLoader;

/**
 * The type Serialization factory.
 */
public class SerializationFactory {


    /**
     * Get rpc serialization.
     * @param serialization the serialization
     * @return the rpc serialization
     * @throws Exception the exception
     */
    public static RpcSerialization get(String serialization) throws Exception {

        return ExtensionLoader.getInstance().get(serialization);

    }

    /**
     * Init.
     * @throws Exception the exception
     */
    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(RpcSerialization.class);
    }
}
