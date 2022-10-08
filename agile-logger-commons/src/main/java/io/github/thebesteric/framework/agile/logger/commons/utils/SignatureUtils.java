package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.lang.reflect.Method;

/**
 * SignatureUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class SignatureUtils {

    public static String methodSignature(Method method) {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

}
