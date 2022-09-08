package io.github.thebesteric.framework.agile.logger.spring;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AbstractAgileLoggerFilter;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerRequestWrapper;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * AgileLoggerFilter
 * <p>Build on 2022-08-17
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public class AgileLoggerFilter extends AbstractAgileLoggerFilter {

    public AgileLoggerFilter(AgileLoggerContext agileLoggerContext, List<RecordProcessor> recordProcessors) {
        super(agileLoggerContext, recordProcessors);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // Check enable
        if (!this.properties.isEnable()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check ignore URI
        String uri = ((HttpServletRequest) request).getRequestURI();
        if (this.ignoreUriProcessor.matching(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check URI legal
        if (!checkLegalUri(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // wrapper request & response
        AgileLoggerRequestWrapper requestWrapper = new AgileLoggerRequestWrapper((HttpServletRequest) request);
        AgileLoggerResponseWrapper responseWrapper = new AgileLoggerResponseWrapper((HttpServletResponse) response);

        initTrackId(requestWrapper, this.properties.isUseSkyWalkingTrace());
        String id = AgileContext.idGenerator.generate();
        AgileLoggerContext.setParentId(id);

        String durationTag = DurationWatcher.start();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception ex) {
            ex.printStackTrace();
            responseWrapper.setException(ex);
            responseWrapper.setBuffer(ex.getMessage());
        } finally {
            DurationWatcher.Duration duration = DurationWatcher.stop(durationTag);

            RequestLog requestLog = this.requestLoggerProcessor.processor(id, requestWrapper, responseWrapper, duration);

            // Record request info
            this.currentRecordProcessor.processor(requestLog);

            ServletOutputStream out = response.getOutputStream();
            out.write(responseWrapper.getByteArray());
            out.flush();

            DurationWatcher.clear();
        }

    }
}
