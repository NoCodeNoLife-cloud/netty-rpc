package org.code.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.code.Filter.FilterConfig;
import org.code.annitation.RpcService;
import org.code.common.RpcServiceNameBuilder;
import org.code.common.ServiceMeta;
import org.code.config.RpcProperties;
import org.code.protocol.codec.RpcDecoder;
import org.code.protocol.codec.RpcEncoder;
import org.code.protocol.handler.service.RpcRequestHandler;
import org.code.protocol.handler.service.ServiceAfterFilterHandler;
import org.code.protocol.handler.service.ServiceBeforeFilterHandler;
import org.code.protocol.serialization.SerializationFactory;
import org.code.registry.RegistryFactory;
import org.code.registry.RegistryService;
import org.code.router.LoadBalancerFactory;
import org.code.utils.PropertiesUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Provider post processor.
 */
@Slf4j
public class ProviderPostProcessor implements InitializingBean, BeanPostProcessor, EnvironmentAware {


    private static String serverAddress;

    static {
        try {
            serverAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, Object> rpcServiceMap = new HashMap<>();
    /**
     * The Rpc properties.
     */
    RpcProperties rpcProperties;

    @Override
    public void afterPropertiesSet() throws Exception {

        Thread t = new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                log.error("start rpc server error.", e);
            }
        });
        t.setDaemon(true);
        t.start();
        SerializationFactory.init();
        RegistryFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initServiceFilter();
    }

    private void startRpcServer() throws InterruptedException {
        int serverPort = rpcProperties.getPort();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder())
                                    .addLast(new ServiceBeforeFilterHandler())
                                    .addLast(new RpcRequestHandler(rpcServiceMap))
                                    .addLast(new ServiceAfterFilterHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, serverPort).sync();
            log.info("server addr {} started on port {}", this.serverAddress, serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * Service Registration
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // Find the class with RpcService annotation on the bean
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // There may be multiple interfaces, the first interface is selected by default
            String serviceName = beanClass.getInterfaces()[0].getName();
            if (!rpcService.serviceInterface().equals(void.class)) {
                serviceName = rpcService.serviceInterface().getName();
            }
            String serviceVersion = rpcService.serviceVersion();
            try {
                // service registration
                int servicePort = rpcProperties.getPort();
                // Get the registration center ioc
                RegistryService registryService = RegistryFactory.get(rpcProperties.getRegisterType());
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceAddr(serverAddress);
                serviceMeta.setServicePort(servicePort);
                serviceMeta.setServiceVersion(serviceVersion);
                serviceMeta.setServiceName(serviceName);
                registryService.register(serviceMeta);
                // cache
                rpcServiceMap.put(RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()), bean);
                log.info("register server {} version {}", serviceName, serviceVersion);
            } catch (Exception e) {
                log.error("failed to register service {}", serviceVersion, e);
            }
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties, environment);
         /*String prefix = "rpc.";
         Integer port = Integer. valueOf(environment. getProperty(prefix + "port"));
         String registerAddr = environment. getProperty(prefix + "register-addr");
         String registerType = environment. getProperty(prefix + "register-type");
         String registerPsw = environment. getProperty(prefix + "register-psw");
         String serialization = environment. getProperty(prefix + "register-serialization");

         RpcProperties properties = RpcProperties. getInstance();
         // Additional extended parameters
         properties.setServiceAttachments(PropertyUtil.handle(environment,prefix+"service.",Map.class));
         properties.setClientAttachments(PropertyUtil.handle(environment,prefix+"client.",Map.class));
         properties.setPort(port);
         properties.setSerialization(serialization);
         properties.setRegisterAddr(registerAddr);
         properties.setRegisterType(registerType);
         properties.setRegisterPsw(registerPsw);*/
        rpcProperties = properties;
        log.info("read configuration file successfully");
    }
}
