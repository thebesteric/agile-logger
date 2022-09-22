package io.github.thebesteric.framework.agile.logger.spring.processor.ignore;

import java.util.Set;

/**
 * DefaultIgnoreMethodProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public class DefaultIgnoreMethodProcessor extends AbstractIgnoreMethodProcessor {
    @Override
    public void addIgnoreMethods(Set<IgnoreMethod> ignoreMethods) {
        addDefaultIgnoreMethods();
    }
}
