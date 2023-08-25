package org.code.Filter.client;

import lombok.extern.slf4j.Slf4j;
import org.code.Filter.ClientBeforeFilter;
import org.code.Filter.FilterData;


/**
 * The type Client log filter.
 */
@Slf4j
public class ClientLogFilter implements ClientBeforeFilter {

    /**
     * Do filter.
     * @param filterData the filter data
     */
    @Override
    public void doFilter(FilterData filterData) {
        log.info(filterData.toString());
    }
}
