package io.github.thebesteric.framework.agile.logger.spring.domain;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * Parent
 *
 * @author Eric Joe
 * @version 1.0
 */
@Getter
@Setter
public class Parent {
    private String id;
    private Method method;
    private Object[] args;

    public Parent(String id, Method method, Object[] args) {
        this.id = id;
        this.method = method;
        this.args = args;
    }
}
