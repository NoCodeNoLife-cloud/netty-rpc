package org.code.Filter.service;

import org.code.Filter.FilterData;
import org.code.Filter.ServiceBeforeFilter;
import org.code.config.RpcProperties;

import java.util.Map;

/**
 * The type Service token filter.
 */
public class ServiceTokenFilter implements ServiceBeforeFilter {

    /**
     * Do filter.
     * @param filterData the filter data
     */
    @Override
    public void doFilter(FilterData filterData) {
        final Map<String, Object> attachments = filterData.getClientAttachments();
        final Map<String, Object> serviceAttachments = RpcProperties.getInstance().getServiceAttachments();
        if (!attachments.getOrDefault("token", "").equals(serviceAttachments.getOrDefault("token", ""))) {
            throw new IllegalArgumentException("token is incorrect");
        }
    }

}
