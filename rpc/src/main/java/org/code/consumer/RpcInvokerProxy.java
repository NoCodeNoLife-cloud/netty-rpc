package org.code.consumer;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.code.Filter.FilterConfig;
import org.code.Filter.FilterData;
import org.code.common.*;
import org.code.common.constants.MsgType;
import org.code.common.constants.ProtocolConstants;
import org.code.config.RpcProperties;
import org.code.protocol.MsgHeader;
import org.code.protocol.RpcProtocol;
import org.code.router.LoadBalancer;
import org.code.router.LoadBalancerFactory;
import org.code.router.ServiceMetaRes;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.code.common.constants.FaultTolerantRules.*;


/**
 * The type Rpc invoker proxy.
 */
@Slf4j
public class RpcInvokerProxy implements InvocationHandler {

    private String serviceVersion;
    private long timeout;
    private String loadBalancerType;
    private String faultTolerantType;
    private long retryCount;


    /**
     * Instantiates a new Rpc invoker proxy.
     * @param serviceVersion    the service version
     * @param timeout           the timeout
     * @param faultTolerantType the fault tolerant type
     * @param loadBalancerType  the load balancer type
     * @param retryCount        the retry count
     * @throws Exception the exception
     */
    public RpcInvokerProxy(String serviceVersion, long timeout, String faultTolerantType, String loadBalancerType, long retryCount) throws Exception {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.loadBalancerType = loadBalancerType;
        this.faultTolerantType = faultTolerantType;
        this.retryCount = retryCount;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        // Building the message header
        MsgHeader header = new MsgHeader();
        long requestId = RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        final byte[] serialization = RpcProperties.getInstance().getSerialization().getBytes();
        header.setSerializationLen(serialization.length);
        header.setSerializations(serialization);
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        // Build the request body
        RpcRequest request = new RpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(ObjectUtils.isEmpty(args) ? new Object[0] : args);
        request.setServiceAttachments(RpcProperties.getInstance().getServiceAttachments());
        request.setClientAttachments(RpcProperties.getInstance().getClientAttachments());
        // The context of the interceptor
        final FilterData filterData = new FilterData(request);
        try {
            FilterConfig.getClientBeforeFilterChain().doFilter(filterData);
        } catch (Throwable e) {
            throw e;
        }
        protocol.setBody(request);

        RpcConsumer rpcConsumer = new RpcConsumer();


        String serviceName = RpcServiceNameBuilder.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object[] params = request.getParams();
        // 1. Get the load balancing strategy
        final LoadBalancer loadBalancer = LoadBalancerFactory.get(loadBalancerType);

        // 2. Obtain the corresponding service according to the policy
        final ServiceMetaRes serviceMetaRes = loadBalancer.select(params, serviceName);

        ServiceMeta curServiceMeta = serviceMetaRes.getCurServiceMeta();
        final Collection<ServiceMeta> otherServiceMeta = serviceMetaRes.getOtherServiceMeta();
        long count = 1;
        long retryCount = this.retryCount;
        RpcResponse rpcResponse = null;
        // Retry mechanism
        while (count <= retryCount) {
            // Processing returned data
            RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
            // XXXHolder
            RpcRequestHolder.REQUEST_MAP.put(requestId, future);
            try {
                // Sending messages
                rpcConsumer.sendRequest(protocol, curServiceMeta);
                // Wait for the response data to return
                rpcResponse = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                // If there is an exception and no other service
                if (rpcResponse.getThrowable() != null && otherServiceMeta.size() == 0) {
                    throw rpcResponse.getThrowable();
                }
                if (rpcResponse.getThrowable() != null) {
                    throw rpcResponse.getThrowable();
                }
                log.info("rpc call succeeded, serviceName: {}", serviceName);
                try {
                    FilterConfig.getClientAfterFilterChain().doFilter(filterData);
                } catch (Throwable e) {
                    throw e;
                }
                return rpcResponse.getData();
            } catch (Throwable e) {
                // Exception Throwable
                String errorMsg = e.getMessage() == null ? e.getCause().getMessage() : e.getMessage();
                // todo The fault-tolerant mechanism here can be expanded, and the job can be changed by itself
                switch (faultTolerantType) {
                    // Fail fast
                    case FailFast:
                        log.warn("rpc call failed, trigger FailFast strategy, exception information: {}", errorMsg);
                        return rpcResponse.getThrowable();
                    // failover
                    case Failover:
                        log.warn("rpc call failed, retry {} time, exception information: {}", count, errorMsg);
                        count++;
                        if (!ObjectUtils.isEmpty(otherServiceMeta)) {
                            final ServiceMeta next = otherServiceMeta.iterator().next();
                            curServiceMeta = next;
                            otherServiceMeta.remove(next);
                        } else {
                            final String msg = String.format("rpc call failed, no service available serviceName: {%s}, exception message: {%s}", serviceName, errorMsg);
                            log.warn(msg);
                            throw new RuntimeException(msg);
                        }
                        break;
                    // Ignore the error
                    case Failsafe:
                        return null;
                }
            }
        }

        throw new RuntimeException("The rpc call failed, the maximum number of retries exceeded: {}" + retryCount);
    }
}