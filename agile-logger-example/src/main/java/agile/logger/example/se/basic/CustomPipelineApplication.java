package agile.logger.example.se.basic;

import agile.logger.example.domain.MathCalc;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLoggerEntrance;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.core.handler.AbstractHeadHandler;
import io.github.thebesteric.framework.agile.logger.core.handler.AbstractTailHandler;
import io.github.thebesteric.framework.agile.logger.core.pipeline.DefaultPipeline;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Node;
import io.github.thebesteric.framework.agile.logger.core.utils.AgileLoggerHelper;

/**
 * CustomHandlerApplication
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-12 14:22:05
 */
@AgileLogger(tag = "application")
public class CustomPipelineApplication {
    static {
        Node internalHead = new Node("custom-head", new AbstractHeadHandler() {
            @Override
            public InvokeLog process(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
                return invokeLog;
            }
        });
        Node internalTail = new Node("custom-tail", new AbstractTailHandler() {
            @Override
            public void process(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
                System.out.println("[custom]: " + invokeLog);
            }
        });
        AgileLoggerHelper.builder()
                .enable(true)
                .logMode(LogMode.STDOUT)
                .createPipeline(new DefaultPipeline(internalHead, internalTail))
                .async(true)
                .asyncExecutePool(1, 2, 10, 1)
                .build();
    }

    @AgileLoggerEntrance
    public static void main(String[] args) throws InterruptedException {
        MathCalc calc = new MathCalc();
        calc.add(1, 2);
        calc.minus(1, 2);
    }
}
