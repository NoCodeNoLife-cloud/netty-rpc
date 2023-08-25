package org.code.annitation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service provider notes
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {


    /**
     * Service interface class.
     * @return the class
     */
    Class<?> serviceInterface() default void.class;

    /**
     * Service version string.
     * @return the string
     */
    String serviceVersion() default "1.0";
}