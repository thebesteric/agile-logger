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

    abstract void addIgnoreUris(Set<String> ignoreUrls);

    @Override
    public void add(Set<String> ignoreUrls) {
        addIgnoreUris(ignoreUrls);
        addDefaultIgnoreUris();
    }
}
