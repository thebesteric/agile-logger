package io.github.thebesteric.framework.agile.logger.core.annotation;

import java.lang.annotation.*;

/**
 * 方法参数重写
 * 作用在方法参数上，可以重写某些敏感信息
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 18:00:33
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RewriteParam {
    String value();
}
