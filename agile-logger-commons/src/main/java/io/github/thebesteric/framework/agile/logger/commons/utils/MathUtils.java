package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.math.BigDecimal;

/**
 * MathUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class MathUtils {

    public static String divStripTrailingZeros(Double d1, Double d2) {
        double result = d1 / d2;
        String resultStr = String.valueOf(result);
        return stripTrailingZeros(resultStr);

    }

    public static String stripTrailingZeros(String str) {
        return new BigDecimal(str).stripTrailingZeros().toPlainString();
    }

}
