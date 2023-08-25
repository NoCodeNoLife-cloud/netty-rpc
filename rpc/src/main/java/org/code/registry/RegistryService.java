package org.code.registry;


import org.code.common.ServiceMeta;

import java.io.IOException;
import java.util.List;

/**
 * The interface Registry service.
 */
public interface RegistryService {

    /**
     * 服务注册
     * @param serviceMeta the service meta
     * @throws Exception the exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务注销
     * @param serviceMeta the service meta
     * @throws Exception the exception
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;


    /**
     * 获取 serviceName 下的所有服务
     * @param serviceName the service name
     * @return list list
     */
    List<ServiceMeta> discoveries(String serviceName);

    /**
     * 关闭
     * @throws IOException the io exception
     */
    void destroy() throws IOException;

}
