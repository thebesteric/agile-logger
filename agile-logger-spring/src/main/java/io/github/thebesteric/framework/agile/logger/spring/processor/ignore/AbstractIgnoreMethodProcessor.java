package io.github.thebesteric.framework.agile.logger.spring.processor.ignore;

import io.github.thebesteric.framework.agile.logger.spring.processor.IgnoreMethodProcessor;

import java.util.Set;

/**
 * AbstractIgnoreMethodProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public abstract class AbstractIgnoreMethodProcessor implements IgnoreMethodProcessor {

    public abstract void addIgnoreMethods(Set<IgnoreMethod> ignoreMethods);

    @Override
    public void add(Set<IgnoreMethod> ignoreMethods) {
        addIgnoreMethods(ignoreMethods);
        addDefaultIgnoreMethods();
    }
}
