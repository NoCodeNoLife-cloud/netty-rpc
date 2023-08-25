package org.code.common;

import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The type Rpc future.
 * @param <T> the type parameter
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RpcFuture<T> {

    private Promise<T> promise;
    private long timeout;
}
