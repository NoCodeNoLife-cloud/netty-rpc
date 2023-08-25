package org.code.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The type Rpc request holder.
 */
public class RpcRequestHolder {

    /**
     * Request id
     */
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    /**
     * Bind request
     */
    public static final Map<Long, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
