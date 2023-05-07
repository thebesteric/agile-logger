package io.github.thebesteric.framework.agile.logger.core;

import io.github.thebesteric.framework.agile.logger.core.utils.DefaultIdGenerator;
import io.github.thebesteric.framework.agile.logger.core.utils.IdGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * AgileContext
 *
 * @author Eric Joe
 * @since 1.0
 */
@Getter
@Setter
public class AgileContext {

    private String trackId;
    private String invokeLogId;
    public static IdGenerator idGenerator;
    public static IdGenerator trackIdGenerator;

    public AgileContext() {
        this.trackId = generateTrackId();
    }

    public String generateTrackId() {
        if (trackIdGenerator == null) {
            trackIdGenerator = DefaultIdGenerator.getInstance();
        }
        return trackIdGenerator.generate();
    }

}
