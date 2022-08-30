package io.github.thebesteric.framework.commons;

import io.github.thebesteric.framework.agile.logger.core.pipeline.DefaultPipeline;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Node;
import org.junit.Assert;
import org.junit.Test;

/**
 * PipelineTest
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-20 23:28:15
 */
public class PipelineTest {

    Node node1 = new Node("1");
    Node node2 = new Node("2");
    Node node3 = new Node("3");
    Node node4 = new Node("4");
    Node node5 = new Node("5");
    Node node6 = new Node("6");
    Node node7 = new Node("7");

    @Test
    public void testPipeline() {

        DefaultPipeline pipeline = new DefaultPipeline(node2, node4);
        pipeline.addLast(node3);
        pipeline.addLast(node1);

        pipeline.addHead(node5);
        pipeline.addTail(node6);

        pipeline.addFirst(node7);

        System.out.println("原始: " + pipeline.getNodes());

        pipeline.remove(node1);
        System.out.println("删除 1: " + pipeline.getNodes());

        pipeline.remove(2);
        System.out.println("删除 2: " + pipeline.getNodes());

        pipeline.remove("5");
        System.out.println("删除 5: " + pipeline.getNodes());

        pipeline.remove("6");
        System.out.println("删除 6: " + pipeline.getNodes());

        pipeline.insert(0, node5);
        System.out.println("添加 5 到 0 位置: " + pipeline.getNodes());

        System.out.println(pipeline.get(1));
        System.out.println(pipeline.get("4"));
        System.out.println(pipeline.get(node4));

        Assert.assertEquals(pipeline.getHead(), node5);
    }

    @Test
    public void testReplace() {
        DefaultPipeline pipeline = new DefaultPipeline(node1, node2);
        pipeline.replaceTail(node3);
        Assert.assertEquals(pipeline.getTail(), node3);

        pipeline.replaceHead(node4);
        Assert.assertEquals(pipeline.getHead(), node4);

        pipeline.addLast(node5);
        Assert.assertEquals(pipeline.get(1), node5);

        System.out.println(pipeline);
    }
}
