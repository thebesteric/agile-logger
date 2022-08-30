package io.github.thebesteric.framework.agile.logger.core.pipeline;

import java.util.List;

/**
 * Pipeline
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-26 10:34:19
 */
public interface Pipeline<T> {

    Pipeline<T> addHead(T node);

    Pipeline<T> addTail(T node);

    Pipeline<T> addFirst(T node);

    Pipeline<T> addLast(T node);

    Pipeline<T> remove(T node);

    Pipeline<T> remove(int index);

    Pipeline<T> remove(String name);

    Pipeline<T> insert(int index, T node);

    Pipeline<T> replace(int index, T node);

    T get(int index);

    T get(T node);

    T get(String name);

    List<T> getNodes();

    List<String> getNodeNames();

    default int size() {
        return getNodes().size();
    }

    default T getHead() {
        return get(0);
    }

    default T getTail() {
        return get(getNodes().size() - 1);
    }

    default boolean isHead(T node) {
        return node == getHead();
    }

    default boolean isTail(T node) {
        return node == getTail();
    }

    default Pipeline<T> replaceHead(T node) {
        return replace(0, node);
    }

    default Pipeline<T> replaceTail(T node) {
        return replace(size() - 1, node);
    }

}
