package org.code.Filter;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.code.common.RpcRequest;
import org.code.common.RpcResponse;

import java.util.Map;

/**
 * The type Filter data.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class FilterData {


    private String serviceVersion;
    private long timeout;
    private long retryCount;
    private String className;
    private String methodName;
    private Object[] args;
    private Map<String, Object> serviceAttachments;
    private Map<String, Object> clientAttachments;
    private RpcResponse data; // The data after executing the business logic

    /**
     * Instantiates a new Filter data.
     * @param request the request
     */
    public FilterData(RpcRequest request) {
        this.args = request.getParams();
        this.className = request.getClassName();
        this.methodName = request.getMethodName();
        this.serviceVersion = request.getServiceVersion();
        this.serviceAttachments = request.getServiceAttachments();
        this.clientAttachments = request.getClientAttachments();
    }
}
