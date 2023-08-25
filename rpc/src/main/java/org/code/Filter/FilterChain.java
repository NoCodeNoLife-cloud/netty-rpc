package org.code.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Filter chain.
 */
public class FilterChain {


    private List<Filter> filters = new ArrayList<>();

    /**
     * Add filter.
     * @param filter the filter
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
    }


    /**
     * Add filter.
     * @param filters the filters
     */
    public void addFilter(List<Object> filters) {
        for (Object filter : filters) {
            addFilter((Filter) filter);
        }
    }

    /**
     * Do filter.
     * @param data the data
     */
    public void doFilter(FilterData data) {
        for (Filter filter : filters) {
            filter.doFilter(data);
        }
    }
}
