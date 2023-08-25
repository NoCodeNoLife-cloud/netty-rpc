package org.code.consumer;

import lombok.extern.slf4j.Slf4j;
import org.code.Filter.FilterConfig;
import org.code.annitation.RpcReference;
import org.code.config.RpcProperties;
import org.code.protocol.serialization.SerializationFactory;
import org.code.registry.RegistryFactory;
import org.code.router.LoadBalancerFactory;
import org.code.utils.PropertiesUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Consumer side post processor
 */
@Configuration
@Slf4j
public class ConsumerPostProcessor implements BeanPostProcessor, EnvironmentAware, InitializingBean {

    /**
     * The Rpc properties.
     */
    RpcProperties rpcProperties;

    /**
     * Read the configuration from the configuration file
     */
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties, environment);
        /*String prefix = "rpc.";
        String registerAddr = environment.getProperty(prefix + "register-addr");
        String registerType = environment.getProperty(prefix + "register-type");
        String registerPsw = environment.getProperty(prefix + "register-psw");
        String serialization = environment.getProperty(prefix + "register-serialization");

        properties.setServiceAttachments(PropertyUtil.handle(environment,prefix+"service.",Map.class));
        properties.setClientAttachments(PropertyUtil.handle(environment,prefix+"client.",Map.class));
        properties.setSerialization(serialization);
        properties.setRegisterAddr(registerAddr);
        properties.setRegisterType(registerType);
        properties.setRegisterPsw(registerPsw);*/
        rpcProperties = properties;
        log.info("The configuration file was read successfully");
    }

    /**
     * Initialize some beans
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        SerializationFactory.init();
        RegistryFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initClientFilter();
    }

    /**
     * Agent layer injection
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Get all fields
        final Field[] fields = bean.getClass().getDeclaredFields();
        // Iterate over all fields to find the field annotated by the RpcReference
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                final RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                final Class<?> aClass = field.getType();
                field.setAccessible(true);
                Object object = null;
                try {
                    // Creating a proxy object
                    object = Proxy.newProxyInstance(
                            aClass.getClassLoader(),
                            new Class<?>[]{aClass},
                            new RpcInvokerProxy(rpcReference.serviceVersion(), rpcReference.timeout(), rpcReference.faultTolerant(),
                                    rpcReference.loadBalancer(), rpcReference.retryCount()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // Set the proxy object to the field
                    field.set(bean, object);
                    field.setAccessible(false);
                    log.info(beanName + " field:" + field.getName() + "注入成功");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    log.info(beanName + " field:" + field.getName() + "注入失败");

                }
            }
        }
        return bean;
    }
}
