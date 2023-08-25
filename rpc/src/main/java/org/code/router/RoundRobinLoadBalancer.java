package org.code.router;


import org.code.common.ServiceMeta;
import org.code.config.RpcProperties;
import org.code.registry.RegistryService;
import org.code.spi.ExtensionLoader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Round robin load balancer.
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private static AtomicInteger roundRobinId = new AtomicInteger(0);

    @Override
    public ServiceMetaRes select(Object[] params, String serviceName) {
        // Get the registration center
        RegistryService registryService = ExtensionLoader.getInstance().get(RpcProperties.getInstance().getRegisterType());
        List<ServiceMeta> discoveries = registryService. discoveries(serviceName);
        // 1. Get all services
        int size = discoveries. size();
        // 2. According to the current polling ID, get the remaining service length to get the specific service
        roundRobinId.addAndGet(1);
        if (roundRobinId.get() == Integer.MAX_VALUE) {
            roundRobinId.set(0);
        }

        return ServiceMetaRes.build(discoveries.get(roundRobinId.get() % size), discoveries);
    }

}