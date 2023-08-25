package org.code.annitation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Suffix
 */
@Retention(RetentionPolicy. RUNTIME)
@Target(ElementType. FIELD)
public @interface PropertiesField {

    /**
     * Defaults to property name Example: registryType = registry-type Follow configuration file rules
     * @return the string
     */
    String value() default "";
}