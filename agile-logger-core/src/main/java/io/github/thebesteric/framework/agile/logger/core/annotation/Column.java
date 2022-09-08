package io.github.thebesteric.framework.agile.logger.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

    String name() default "";

    Type type() default Type.VARCHAR;

    int length() default 255;

    boolean unsigned() default false;

    boolean unique() default false;

    String comment() default "";

    boolean nullable() default true;

    int version() default 0;

    enum Type {

        TINY_INT("TINYINT"), INT("INT"), SMALL_INT("SMALLINT"),BIG_INT("BIGINT"),
        VARCHAR("VARCHAR"), JSON("JSON"), DATETIME("DATETIME");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
