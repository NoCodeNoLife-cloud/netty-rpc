package org.code.common;

/**
 * The type Rpc service name builder.
 */
public class RpcServiceNameBuilder {


    /**
     * key: service name value: service provider
     * @param serviceName    the service name
     * @param serviceVersion the service version
     * @return the string
     */
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("$", serviceName, serviceVersion);
    }
}
