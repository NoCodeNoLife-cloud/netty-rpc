package org.code.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.code.common.RpcRequest;
import org.code.common.RpcResponse;
import org.code.common.constants.MsgType;
import org.code.common.constants.ProtocolConstants;
import org.code.protocol.MsgHeader;
import org.code.protocol.RpcProtocol;
import org.code.protocol.serialization.RpcSerialization;
import org.code.protocol.serialization.SerializationFactory;

import java.util.List;

/**
 * The type Rpc decoder.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        // If the number of readable bytes is less than the length of the protocol header, it means that the entire protocol header has not been received, and returns directly
        if (in. readableBytes() < ProtocolConstants. HEADER_TOTAL_LEN) {
            return;
        }
        // Mark the current reading position for easy rollback later
        in.markReaderIndex();

        // read the magic number field
        short magic = in. readShort();
        if (magic != ProtocolConstants. MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        // read the version field
        byte version = in. readByte();
        // read message type
        byte msgType = in. readByte();
        // read response status
        byte status = in. readByte();
        // read request ID
        long requestId = in. readLong();
        // Get the length of the serialization algorithm
        final int len = in. readInt();
        if (in. readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }
        final byte[] bytes = new byte[len];
        in. readBytes(bytes);
        final String serialization = new String(bytes);
        // read message body length
        int dataLength = in. readInt();
        // If the number of readable bytes is less than the length of the message body, it means that the entire message body has not been received, roll back and return
        if (in. readableBytes() < dataLength) {
            // rollback mark position
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        // read data
        in. readBytes(data);

        // type of message to be processed
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        // build message header
        MsgHeader header = new MsgHeader();
        header. setMagic(magic);
        header. setVersion(version);
        header. setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializations(bytes);
        header.setSerializationLen(len);
        header.setMsgLen(dataLength);
        // get the serializer
        RpcSerialization rpcSerialization = SerializationFactory. get(serialization);
        // Process according to the message type (if there are too many message types, you can use strategy + factory mode to manage)
        switch (msgTypeEnum) {
            // request message
            case REQUEST:
                RpcRequest request = rpcSerialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol. setHeader(header);
                    protocol.setBody(request);
                    out. add(protocol);
                }
                break;
            // response message
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol. setHeader(header);
                    protocol.setBody(response);
                    out. add(protocol);
                }
                break;
        }
    }
}
