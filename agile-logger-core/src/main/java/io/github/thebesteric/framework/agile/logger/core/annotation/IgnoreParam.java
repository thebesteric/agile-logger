package io.github.thebesteric.framework.agile.logger.core.annotation;

import java.lang.annotation.*;

/**
 * 属性、参数忽略
 * 作用在类或方法参数上，可以忽略该属性或参数
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 18:00:33
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreParam {

}
