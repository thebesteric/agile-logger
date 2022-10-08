package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor;

/**
 * MockOrderEnum
 *
 * @author Eric Joe
 * @version 1.0
 */
public enum MockOrderEnum {

    VALUE_ORDER(1), TYPE_ORDER(2), TARGET_ORDER(3);

    private final int order;

    MockOrderEnum(int order) {
        this.order = order;
    }

    public int order() {
        return this.order;
    }
}
