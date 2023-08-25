package org.code.router;


import org.code.common.ServiceMeta;
import org.code.config.RpcProperties;
import org.code.registry.RegistryService;
import org.code.spi.ExtensionLoader;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The type Consistent hash load balancer.
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    // The virtual node mapped to the physical node, in order to solve the hash skew
    private final static int VIRTUAL_NODE_SIZE = 10;
    private final static String VIRTUAL_NODE_SPLIT = "$";

    @Override
    public ServiceMetaRes select(Object[] params, String serviceName) {
        // Get the registration center
        RegistryService registryService = ExtensionLoader.getInstance().get(RpcProperties.getInstance().getRegisterType());
        List<ServiceMeta> discoveries = registryService. discoveries(serviceName);

        final ServiceMeta curServiceMeta = allocateNode(makeConsistentHashRing(discoveries), params[0].hashCode());
        return ServiceMetaRes.build(curServiceMeta, discoveries);
    }


    private ServiceMeta allocateNode(TreeMap<Integer, ServiceMeta> ring, int hashCode) {
        // Get the nearest node position on the hash ring
        Map.Entry<Integer, ServiceMeta> entry = ring.ceilingEntry(hashCode);
        if (entry == null) {
            // use the smallest node if not found
            entry = ring. firstEntry();
        }
        return entry. getValue();
    }

    /**
     * Add all service instances to the consistent hash ring and generate virtual nodes
     * Here each call needs to build a hash ring for expansion (service provider)
     * @param servers list of service instances
     * @return consistent hash ring
     */
    private TreeMap<Integer, ServiceMeta> makeConsistentHashRing(List<ServiceMeta> servers) {
        TreeMap<Integer, ServiceMeta> ring = new TreeMap<>();
        for (ServiceMeta instance : servers) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                ring.put((buildServiceInstanceKey(instance) + VIRTUAL_NODE_SPLIT + i).hashCode(), instance);
            }
        }
        return ring;
    }

    /**
     * Build a cache key based on service instance information
     */
    private String buildServiceInstanceKey(ServiceMeta serviceMeta) {

        return String.join(":", serviceMeta.getServiceAddr(), String.valueOf(serviceMeta.getServicePort()));
    }
}
