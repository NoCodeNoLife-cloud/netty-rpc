package org.code.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.code.protocol.MsgHeader;
import org.code.protocol.RpcProtocol;
import org.code.protocol.serialization.RpcSerialization;
import org.code.protocol.serialization.SerializationFactory;

/**
 * The type Rpc encoder.
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        // Get the message header type
        MsgHeader header = msg. getHeader();
        // Write the magic number (safety check, you can refer to CAFEBABE in java)
        byteBuf.writeShort(header.getMagic());
        // write version number
        byteBuf.writeByte(header.getVersion());
        // Write message type (receiver performs different processing methods according to different message types)
        byteBuf.writeByte(header.getMsgType());
        // write state
        byteBuf.writeByte(header.getStatus());
        // Write the request id (the request id can be used to record the asynchronous callback identifier, which request needs to be called back)
        byteBuf.writeLong(header.getRequestId());
        // Write the serialization method (the receiver needs to rely on which serialization to serialize)
        byteBuf.writeInt(header.getSerializationLen());
        final byte[] ser = header. getSerializations();
        final String serialization = new String(ser);
        byteBuf.writeBytes(ser);
        RpcSerialization rpcSerialization = SerializationFactory. get(serialization);
        byte[] data = rpcSerialization. serialize(msg. getBody());
        // Write data length (the receiver reads the data content according to the data length)
        byteBuf.writeInt(data.length);
        // data input
        byteBuf.writeBytes(data);
    }
}
