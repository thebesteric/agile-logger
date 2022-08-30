package io.github.thebesteric.framework.agile.logger.core.pipeline;

import io.github.thebesteric.framework.agile.logger.commons.exception.DataAlreadyExistsException;
import io.github.thebesteric.framework.agile.logger.commons.exception.DataNotExistsException;
import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.core.handler.Handler;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DefaultPipeline
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-26 10:46:16
 */
public class DefaultPipeline implements Pipeline<Node<Handler>> {

    private Node<Handler> head;
    private Node<Handler> tail;

    public DefaultPipeline() {
        super();
    }

    public DefaultPipeline(Node<Handler> head, Node<Handler> tail) {
        this();
        this.head = head;
        this.tail = tail;
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public Pipeline<Node<Handler>> addHead(Node<Handler> node) {
        checkNodeName(node);
        Node<Handler> head = this.head;
        this.head = node;
        this.head.next = head;
        if (this.head.next == null) {
            this.head.next = this.tail;
            if (this.head.next != null) {
                this.tail.prev = this.head;
            }
        }
        return this;
    }

    @Override
    public Pipeline<Node<Handler>> addTail(Node<Handler> node) {
        checkNodeName(node);
        Node<Handler> tail = this.tail;
        this.tail = node;
        this.tail.prev = tail;
        if (this.tail.prev == null) {
            this.tail.prev = this.head;
            if (this.tail.prev != null) {
                this.head.next = this.tail;
            }
        }
        return this;
    }

    @Override
    public Pipeline<Node<Handler>> addFirst(Node<Handler> node) {
        checkNodeName(node);
        Node<Handler> next = head.next;
        head.next = node;
        node.prev = head;
        next.prev = node;
        node.next = next;
        return this;
    }

    @Override
    public Pipeline<Node<Handler>> addLast(Node<Handler> node) {
        checkNodeName(node);
        Node<Handler> prev = tail.prev;
        tail.prev = node;
        node.next = tail;
        prev.next = node;
        node.prev = prev;
        return this;
    }

    @Override
    public Pipeline<Node<Handler>> insert(int index, Node<Handler> node) {
        checkNodeName(node);
        Node<Handler> origin = get(index);
        if (origin == null) {
            throw new DataNotExistsException("Insert node is not exists at index: %d", index);
        }
        Node<Handler> prev = origin.prev;
        node.next = origin;
        if (prev != null) {
            prev.next = node;
            node.prev = prev;
            origin.prev = node;
        } else {
            this.head = node;
            this.head.next = origin;
            origin.prev = this.head;
        }
        return this;
    }

    @Override
    public Pipeline<Node<Handler>> replace(int index, Node<Handler> node) {
        Node<Handler> origin = get(index);
        Node<Handler> prev = origin.prev;
        Node<Handler> next = origin.next;
        if (prev != null) {
            prev.next = node;
            node.prev = prev;
        } else {
            this.head = node;
        }

        if (next != null) {
            next.prev = node;
            node.next = next;
        } else {
            this.tail = node;
        }
        return this;
    }

    @Override
    public Node<Handler> get(int index) {
        List<Node<Handler>> nodes = getNodes();
        if (index >= nodes.size()) {
            return null;
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (i == index) {
                return nodes.get(i);
            }
        }
        return null;
    }

    @Override
    public Node<Handler> get(Node<Handler> node) {
        return getNodes().stream().filter((n -> n == node)).findFirst().orElse(null);
    }

    @Override
    public Node<Handler> get(String name) {
        return getNodes().stream().filter((n -> n.getName().equals(name))).findFirst().orElse(null);
    }

    @Override
    public Pipeline<Node<Handler>> remove(Node<Handler> node) {
        if (node != null) {
            Node<Handler> prev = node.prev;
            Node<Handler> next = node.next;
            if (prev != null) {
                prev.next = next;
            } else {
                this.head = next;
                next.prev = null;
            }
            if (next != null) {
                next.prev = prev;
            } else {
                this.tail = prev;
                prev.next = null;
            }
        }
        return this;
    }

    @Override
    public Pipeline<Node<Handler>> remove(int index) {
        Node<Handler> node = null;
        if (index < getNodes().size()) {
            node = getNodes().get(index);
        }
        return remove(node);
    }

    @Override
    public Pipeline<Node<Handler>> remove(String name) {
        if (StringUtils.isNotEmpty(name)) {
            getNodes().stream().filter((node -> name.equals(node.getName()))).findFirst().ifPresent((this::remove));
        }
        return this;
    }

    @Override
    public List<Node<Handler>> getNodes() {
        List<Node<Handler>> nodes = new ArrayList<>();
        Node<Handler> curNode = this.head;
        while (curNode != null) {
            nodes.add(curNode);
            curNode = curNode.next;
        }
        return nodes;
    }

    @Override
    public List<String> getNodeNames() {
        List<Node<Handler>> nodes = getNodes();
        return nodes.stream().map(Node::getName).collect(Collectors.toList());
    }

    private void checkNodeName(Node<Handler> node) {
        List<String> nodeNames = getNodeNames();
        if (CollectionUtils.isNotEmpty(nodeNames) && nodeNames.contains(node.getName())) {
            throw new DataAlreadyExistsException("Node name already exists: %s", node.getName());
        }
    }
}
