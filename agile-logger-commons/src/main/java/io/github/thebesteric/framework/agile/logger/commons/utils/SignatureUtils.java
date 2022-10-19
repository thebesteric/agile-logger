package io.github.thebesteric.framework.agile.logger.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * SignatureUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class SignatureUtils {

    public static String methodSignature(Method method) {
        String modifiers = StringUtils.join(ReflectUtils.getModifiers(method), " ");
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            args.append(method.getParameterTypes()[i].getName());
            if (i < method.getParameterTypes().length - 1)
                args.append(",");
        }
        return modifiers + " " + method.getDeclaringClass().getName() + "#" + method.getName() + "(" + args + ")";
    }

}
