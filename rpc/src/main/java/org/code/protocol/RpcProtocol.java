package org.code.protocol;

import lombok.*;

import java.io.Serializable;

/**
 * The type Rpc protocol.
 * @param <T> the type parameter
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class RpcProtocol<T> implements Serializable {

    private MsgHeader header;
    private T body;
}
