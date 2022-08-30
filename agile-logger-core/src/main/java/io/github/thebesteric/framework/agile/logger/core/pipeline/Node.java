package io.github.thebesteric.framework.agile.logger.core.pipeline;

import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;

/**
 * Node
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-26 10:34:57
 */
public class Node<H> {

    private String name;
    protected volatile Node<H> next;
    protected volatile Node<H> prev;
    private H handler;

    public Node(String name) {
        this.name = name;
    }

    public Node(H handler) {
        this(StringUtils.toLowerFirst(handler.getClass().getSimpleName()), handler);
    }

    public Node(String name, H handler) {
        this(name);
        this.handler = handler;
    }

    @Override
    public String toString() {
        return name;
    }

    /* getter and setter */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public H getHandler() {
        return handler;
    }

    public void setHandler(H handler) {
        this.handler = handler;
    }

    public Node<? extends H> getNext() {
        return next;
    }

    public void setNext(Node<H> next) {
        this.next = next;
    }

    public Node<?> getPrev() {
        return prev;
    }

    public void setPrev(Node<H> prev) {
        this.prev = prev;
    }
}
