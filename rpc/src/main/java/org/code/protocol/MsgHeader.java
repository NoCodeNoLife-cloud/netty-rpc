package org.code.protocol;

import lombok.*;

import java.io.Serializable;

/**
 * The type Msg header.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class MsgHeader implements Serializable {

    private short magic; // magic number
    private byte version; // protocol version number
    private byte msgType; // data type
    private byte status; // status
    private long requestId; // request ID
    private int serializationLen;
    private byte[] serializations;
    private int msgLen; // data length
}