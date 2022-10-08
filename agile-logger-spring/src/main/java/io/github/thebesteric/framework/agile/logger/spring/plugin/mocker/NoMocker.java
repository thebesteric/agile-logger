package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import io.github.thebesteric.framework.agile.logger.spring.domain.R;

/**
 * NoMocker
 *
 * @author Eric Joe
 * @version 1.0
 */
public class NoMocker implements MockerAdapter<R> {
    @Override
    public R mock() {
        return null;
    }
}
