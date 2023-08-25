package org.code.router;


import org.code.common.ServiceMeta;

import java.util.Collection;

/**
 * The type Service meta res.
 */
public class ServiceMetaRes {

    // 当前服务节点
    private ServiceMeta curServiceMeta;

    // 剩余服务节点
    private Collection<ServiceMeta> otherServiceMeta;

    /**
     * Gets other service meta.
     * @return the other service meta
     */
    public Collection<ServiceMeta> getOtherServiceMeta() {
        return otherServiceMeta;
    }

    /**
     * Gets cur service meta.
     * @return the cur service meta
     */
    public ServiceMeta getCurServiceMeta() {
        return curServiceMeta;
    }

    /**
     * Build service meta res.
     * @param curServiceMeta   the cur service meta
     * @param otherServiceMeta the other service meta
     * @return the service meta res
     */
    public static ServiceMetaRes build(ServiceMeta curServiceMeta, Collection<ServiceMeta> otherServiceMeta) {
        final ServiceMetaRes serviceMetaRes = new ServiceMetaRes();
        serviceMetaRes.curServiceMeta = curServiceMeta;
        otherServiceMeta.remove(curServiceMeta);
        serviceMetaRes.otherServiceMeta = otherServiceMeta;
        return serviceMetaRes;
    }

}
