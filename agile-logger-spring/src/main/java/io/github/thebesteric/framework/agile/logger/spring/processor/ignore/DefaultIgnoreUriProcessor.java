package io.github.thebesteric.framework.agile.logger.spring.processor.ignore;

import java.util.Set;

/**
 * DefaultIgnoreUriProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public class DefaultIgnoreUriProcessor extends AbstractIgnoreUriProcessor {
    @Override
    void addIgnoreUris(Set<String> ignoreUrls) {
        addDefaultIgnoreUris();
    }
}
