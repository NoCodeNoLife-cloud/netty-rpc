package org.code.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.code.common.RpcRequest;
import org.code.common.RpcResponse;
import org.code.common.RpcServiceNameBuilder;
import org.code.common.constants.MsgStatus;
import org.code.common.constants.MsgType;
import org.code.protocol.MsgHeader;
import org.code.protocol.RpcProtocol;
import org.code.protocol.handler.RpcRequestProcessor;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;

/**
 * The type Rpc request handler.
 */
@Setter
@Getter
@AllArgsConstructor
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    // cache service
    private final Map<String, Object> rpcServiceMap;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {

        // asynchronous processing
        RpcRequestProcessor.submitRequest(() -> {
            RpcProtocol<RpcResponse> resProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            MsgHeader header = protocol.getHeader();
            header.setMsgType((byte) MsgType.RESPONSE.ordinal());
            final RpcRequest request = protocol.getBody();
            try {
                // Execute specific business
                Object result = handle(request);
                response.setData(result);
                header.setStatus((byte) MsgStatus.SUCCESS.ordinal());
            } catch (Throwable throwable) {
                // If the execution of the business fails, an exception will be returned
                header.setStatus((byte) MsgStatus.FAILED.ordinal());
                response.setThrowable(throwable);
                log.error("process request {} error", header.getRequestId(), throwable);
            }
            resProtocol.setHeader(header);
            resProtocol.setBody(response);

            log.info("Execution succeeded:", request.getClassName() + request.getMethodName() + request.getServiceVersion());
            ctx.fireChannelRead(resProtocol);
        });
    }

    // call method
    private Object handle(RpcRequest request) throws Throwable {
        String serviceKey = RpcServiceNameBuilder.buildServiceKey(request.getClassName(), request.getServiceVersion());
        // Get service information
        Object serviceBean = rpcServiceMap.get(serviceKey);

        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }

        // Get service provider information and create
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParams();

        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        // call the method and return the result
        return fastClass.invoke(methodIndex, serviceBean, parameters);
    }
}