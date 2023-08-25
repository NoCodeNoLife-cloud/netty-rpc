package org.code.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.code.Filter.FilterConfig;
import org.code.Filter.FilterData;
import org.code.common.RpcRequest;
import org.code.common.RpcResponse;
import org.code.common.constants.MsgStatus;
import org.code.protocol.MsgHeader;
import org.code.protocol.RpcProtocol;

/**
 * The type Service before filter handler.
 */
@Slf4j
public class ServiceBeforeFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        final RpcRequest request = protocol.getBody();
        final FilterData filterData = new FilterData(request);
        RpcResponse response = new RpcResponse();
        MsgHeader header = protocol.getHeader();
        try {
            FilterConfig.getServiceBeforeFilterChain().doFilter(filterData);
        } catch (Throwable throwable) {
            RpcProtocol<RpcResponse> resProtocol = new RpcProtocol<>();
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            response.setThrowable(throwable);
            log.error("before process request {} error", header.getRequestId(), throwable);
            resProtocol.setHeader(header);
            resProtocol.setBody(response);
            ctx.writeAndFlush(resProtocol);
            return;
        }
        ctx.fireChannelRead(protocol);
    }
}
