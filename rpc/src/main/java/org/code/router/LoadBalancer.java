package org.code.router;

/**
 * The interface Load balancer.
 * @param <T> the type parameter
 */
public interface LoadBalancer<T> {

    /**
     * Select load balancing strategy
     * @param params      input parameters, you can customize the load strategy after getting the input parameters
     * @param serviceName service key
     * @return current service node and other nodes, used for fault tolerance
     */
    ServiceMetaRes select(Object[] params, String serviceName);

}
