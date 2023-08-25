package org.code.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * Request body
 */
@Getter
@Setter
public class RpcRequest implements Serializable {

    private String serviceVersion;
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
    private Map<String, Object> serviceAttachments;
    private Map<String, Object> clientAttachments;
}
