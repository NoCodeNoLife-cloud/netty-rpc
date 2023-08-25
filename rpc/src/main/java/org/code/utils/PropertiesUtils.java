package org.code.utils;

import org.code.annitation.PropertiesField;
import org.code.annitation.PropertiesPrefix;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;

/**
 * The type Properties utils.
 */
public class PropertiesUtils {

    /**
     * Match configuration files against the configuration in the object
     * @param o           the o
     * @param environment the environment
     */
    public static void init(Object o, Environment environment) {
        final Class<?> aClass = o.getClass();
        // get prefix
        final PropertiesPrefix prefixAnnotation = aClass.getAnnotation(PropertiesPrefix.class);
        if (prefixAnnotation == null) {
            throw new NullPointerException(aClass + " @PropertiesPrefix does not exist");
        }
        String prefix = prefixAnnotation.value();
        // prefix parameter correction
        if (!prefix.contains(".")) {
            prefix += ".";
        }
        // Loop through the fields in the object
        for (Field field : aClass.getDeclaredFields()) {
            final PropertiesField fieldAnnotation = field.getAnnotation(PropertiesField.class);
            if (fieldAnnotation == null) continue;
            String fieldValue = fieldAnnotation.value();
            if (fieldValue == null || fieldValue.equals("")) {
                fieldValue = convertToHyphenCase(field.getName());
            }
            try {
                field.setAccessible(true);
                final Class<?> type = field.getType();
                final Object value = PropertyUtil.handle(environment, prefix + fieldValue, type);
                if (value == null) continue;
                field.set(o, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }


    /**
     * Convert to hyphen case string.
     * @param input the input
     * @return the string
     */
    public static String convertToHyphenCase(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                output.append('-');
                output.append(Character.toLowerCase(c));
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }
}