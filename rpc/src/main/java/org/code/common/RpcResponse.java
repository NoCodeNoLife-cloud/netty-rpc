package org.code.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The type Rpc response.
 */
@Getter
@Setter
public class RpcResponse implements Serializable {

    private Object data;
    private String message;
    private Throwable throwable;
}
