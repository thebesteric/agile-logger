package io.github.thebesteric.framework.agile.logger.spring;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.processor.IgnoreMethodProcessor;
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
import java.lang.reflect.Method;
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

        // Add uri prefix if present
        String uriPrefix = this.agileLoggerContext.getProperties().getUriPrefix();
        if (uriPrefix != null) {
            uri = uri.substring(uriPrefix.length());
        }

        // Check IgnoreUriProcessor
        if (this.ignoreUriProcessor.matching(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check ignore Method
        Method method = AbstractAgileLoggerFilter.URL_MAPPING.get(uri);
        IgnoreMethodProcessor.IgnoreMethod ignoreMethod = null;
        if (method != null) {
            ignoreMethod = IgnoreMethodProcessor.IgnoreMethod.builder().clazz(method.getDeclaringClass()).method(method).build();
        }
        if (ignoreMethod == null || ignoreMethodProcessor.matching(ignoreMethod)) {
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

        // Initialize: IdGenerator, TrackIdGenerator
        initConfigProperties(requestWrapper);

        String id = AgileContext.idGenerator.generate();
        AgileLoggerContext.setParent(new Parent(id, method, null));

        String durationTag = DurationWatcher.start();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception ex) {
            // Program exceptions
            responseWrapper.setException(ex);
            responseWrapper.setBuffer(ex.getMessage());
            throw ex;
        } finally {
            DurationWatcher.Duration duration = DurationWatcher.stop(durationTag);

            // Create RequestLog
            RequestLog requestLog = this.requestLoggerProcessor.processor(id, requestWrapper, responseWrapper, duration);

            // Process non-program exceptions, For example: code != 200
            String exception = this.agileLoggerContext.getResponseSuccessDefineProcessor().processor(requestLog.getResult());
            if (exception != null) {
                requestLog.setException(exception);
                requestLog.setLevel(InvokeLog.LEVEL_ERROR);
            }

            // Record request info
            this.currentRecordProcessor.processor(requestLog);

            DurationWatcher.clear();

            ServletOutputStream out = response.getOutputStream();
            out.write(responseWrapper.getByteArray());
            out.flush();
        }

    }
}
