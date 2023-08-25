package org.code.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.code.Filter.FilterConfig;
import org.code.Filter.FilterData;
import org.code.Filter.client.ClientLogFilter;
import org.code.common.RpcResponse;
import org.code.common.constants.MsgStatus;
import org.code.protocol.MsgHeader;
import org.code.protocol.RpcProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Service after filter handler.
 */
public class ServiceAfterFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) {
        final FilterData filterData = new FilterData();
        filterData.setData(protocol.getBody());
        RpcResponse response = new RpcResponse();
        MsgHeader header = protocol.getHeader();
        try {
            FilterConfig.getServiceAfterFilterChain().doFilter(filterData);
        } catch (Throwable throwable) {
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            response.setThrowable(throwable);
            logger.error("after process request {} error", header.getRequestId(), throwable);
        }
        ctx.writeAndFlush(protocol);
    }
}
