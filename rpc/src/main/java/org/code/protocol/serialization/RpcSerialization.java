package org.code.protocol.serialization;

import java.io.IOException;

/**
 * The interface Rpc serialization.
 */
public interface RpcSerialization {

    /**
     * Serialize byte [ ].
     * @param <T> the type parameter
     * @param obj the obj
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * Deserialize t.
     * @param <T>  the type parameter
     * @param data the data
     * @param clz  the clz
     * @return the t
     * @throws IOException the io exception
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
