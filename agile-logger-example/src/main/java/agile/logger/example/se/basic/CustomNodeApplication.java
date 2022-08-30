package agile.logger.example.se.basic;

import agile.logger.example.domain.MathCalc;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLoggerEntrance;
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
public class CustomNodeApplication {
    static {
        AgileLoggerHelper.builder()
                .enable(true)
                .createDefaultPipeline()
                .addLastNode(new Node<>("my1", (ctx, joinMethod, invokeLog) -> {
                    System.out.println("[my1]" + joinMethod.getMethod().getName());
                    return invokeLog;
                }))
                .addLastNode(new Node<>("my2", (ctx, joinMethod, invokeLog) -> {
                    System.out.println("[my2]" + joinMethod.getMethod().getName());
                    return invokeLog;
                }))
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
