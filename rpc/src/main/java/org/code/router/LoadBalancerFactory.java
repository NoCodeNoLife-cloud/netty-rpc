package org.code.router;


import org.code.spi.ExtensionLoader;

/**
 * The type Load balancer factory.
 */
public class LoadBalancerFactory {

    /**
     * Get load balancer.
     * @param serviceLoadBalancer the service load balancer
     * @return the load balancer
     * @throws Exception the exception
     */
    public static LoadBalancer get(String serviceLoadBalancer) throws Exception {

        return ExtensionLoader.getInstance().get(serviceLoadBalancer);

    }

    /**
     * Init.
     * @throws Exception the exception
     */
    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(LoadBalancer.class);
    }

}
