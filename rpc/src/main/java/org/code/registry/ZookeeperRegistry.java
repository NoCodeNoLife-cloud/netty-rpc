package org.code.registry;

import org.code.common.RpcServiceNameBuilder;
import org.code.common.ServiceMeta;
import org.code.config.RpcProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Zookeeper registry.
 */
public class ZookeeperRegistry implements RegistryService {

    /**
     * The constant BASE_SLEEP_TIME_MS.
     */
// 连接失败等待重试时间
    public static final int BASE_SLEEP_TIME_MS = 1000;
    /**
     * The constant MAX_RETRIES.
     */
// 重试次数
    public static final int MAX_RETRIES = 3;
    /**
     * The constant ZK_BASE_PATH.
     */
// 跟路径
    public static final String ZK_BASE_PATH = "/xhy_rpc";

    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * 启动zk
     * @throws Exception the exception
     */
    public ZookeeperRegistry() throws Exception {
        String registerAddr = RpcProperties.getInstance().getRegisterAddr();
        CuratorFramework client = CuratorFrameworkFactory.newClient(registerAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }


    /**
     * 服务注册
     * @param serviceMeta 服务数据
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);

    }

    /**
     * 服务注销
     */
    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }


    private List<ServiceMeta> listServices(String serviceName) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        List<ServiceMeta> serviceMetas = serviceInstances.stream().map(serviceMetaServiceInstance -> serviceMetaServiceInstance.getPayload()).collect(Collectors.toList());
        return serviceMetas;
    }

    @Override
    public List<ServiceMeta> discoveries(String serviceName) {
        try {
            return listServices(serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 关闭
     */
    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }

}
