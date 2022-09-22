package io.github.thebesteric.framework.agile.logger.spring.processor.ignore;

import io.github.thebesteric.framework.agile.logger.spring.processor.IgnoreUriProcessor;

import java.util.Set;

/**
 * AbstractIgnoreUriProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public abstract class AbstractIgnoreUriProcessor implements IgnoreUriProcessor {

    public abstract void addIgnoreUris(Set<String> ignoreUris);

    @Override
    public void add(Set<String> ignoreUris) {
        addIgnoreUris(ignoreUris);
        addDefaultIgnoreUris();
    }
}
