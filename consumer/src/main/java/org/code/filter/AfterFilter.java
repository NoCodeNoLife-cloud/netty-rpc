package org.code.filter;

import org.code.Filter.ClientAfterFilter;
import org.code.Filter.FilterData;

/**
 * The type After filter.
 */
public class AfterFilter implements ClientAfterFilter {
	/**
	 * Do filter.
	 * @param filterData the filter data
	 */
	@Override
	public void doFilter(FilterData filterData) {
		System.out.println("客户端后置处理器启动咯");
	}
}
