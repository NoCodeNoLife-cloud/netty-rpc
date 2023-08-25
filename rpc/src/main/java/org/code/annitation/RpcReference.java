package org.code.annitation;

import org.code.common.constants.FaultTolerantRules;
import org.code.common.constants.LoadBalancerRules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service caller annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {


    /**
     * Service version string.
     * @return the string
     */
    String serviceVersion() default "1.0";

    /**
     * Timeout long.
     * @return the long
     */
    long timeout() default 5000;

    /**
     * Load balancer string.
     * @return the string
     */
    String loadBalancer() default LoadBalancerRules.RoundRobin;

    /**
     * Fault tolerant string.
     * @return the string
     */
    String faultTolerant() default FaultTolerantRules.FailFast;

    /**
     * Retry count long.
     * @return the long
     */
    long retryCount() default 3;
}
