package agile.logger.example.domain;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;

import java.util.concurrent.TimeUnit;

/**
 * 数学计算
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022/7/25
 */
@AgileLogger(prefix = "[UPSTREAM]", tag = "math")
// @IgnoreMethods({"add", "minus"})
public class MathCalc {

    @AgileLogger(extra = "add calc")
    public double add(double n1, double n2) throws InterruptedException {
        System.out.println("add");
        double result = n1 + n2;
        foo(result);
        return result;
    }

    // @IgnoreMethod
    public double minus(double n1, double n2) throws InterruptedException {
        System.out.println("minus");
        TimeUnit.MILLISECONDS.sleep(500);
        return n1 - n2;
    }

    @AgileLogger(level = AbstractEntity.LEVEL_DEBUG)
    void foo(double result) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
        System.out.println("result = " + result);
        bar(new Student("eric", 18));
    }

    Student bar(Student student) {
        return student;
    }
}
