package io.github.thebesteric.framework.agile.logger.spring.wrapper;

import io.github.thebesteric.framework.agile.logger.commons.exception.InvalidDataException;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.*;
import io.github.thebesteric.framework.agile.logger.spring.processor.ignore.DefaultIgnoreMethodProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.ignore.DefaultIgnoreUriProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.invoke.DefaultInvokeLoggerProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.record.StdoutRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.request.DefaultRequestLoggerProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.response.DefaultResponseSuccessDefineProcessorProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * SpringApplicationContenxt
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
@Getter
public class AgileLoggerContext {

    public final GenericApplicationContext applicationContext;

    private static final ThreadLocal<String> parentId = new ThreadLocal<>();

    private final AgileLoggerSpringProperties properties;
    private final IgnoreMethodProcessor ignoreMethodProcessor;
    private final IgnoreUriProcessor ignoreUriProcessor;
    private final ResponseSuccessDefineProcessor responseSuccessDefineProcessor;
    private final RequestLoggerProcessor requestLoggerProcessor;
    private final InvokeLoggerProcessor invokeLoggerProcessor;
    private final ExecutorService recordLoggerThreadPool;
    private final Environment environment;

    private List<RecordProcessor> recordProcessors;

    @Setter
    private RecordProcessor currentRecordProcessor;

    public AgileLoggerContext(ApplicationContext applicationContext) {
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.properties = getBean(AgileLoggerSpringProperties.class);
        this.ignoreMethodProcessor = generateIgnoreMethodProcessor();
        this.ignoreUriProcessor = generateIgnoreUriProcessor();
        this.responseSuccessDefineProcessor = generateResponseSuccessDefineProcessor();
        this.requestLoggerProcessor = getBeanOrDefault(RequestLoggerProcessor.class, new DefaultRequestLoggerProcessor());
        this.invokeLoggerProcessor = getBeanOrDefault(InvokeLoggerProcessor.class, new DefaultInvokeLoggerProcessor());
        this.recordLoggerThreadPool = generateExecutorService();
        this.environment = getBean(Environment.class);
    }

    public static void setParentId(String id) {
        AgileLoggerContext.parentId.set(id);
    }

    public static String getParentId() {
        String id = AgileLoggerContext.parentId.get();
        AgileLoggerContext.parentId.remove();
        return id;
    }

    public int getServerPort() {
        String serverPort = this.environment.getProperty("server.port", "8080");
        return Integer.parseInt(serverPort);
    }

    /**
     * <p>Set recordProcessors and find out currentRecordProcessor
     *
     * @param recordProcessors List<RecordProcessor>
     * @return RecordProcessor {@link RecordProcessor}
     */
    public RecordProcessor setRecordProcessors(List<RecordProcessor> recordProcessors) {
        this.recordProcessors = recordProcessors;
        this.currentRecordProcessor = this.recordProcessors.stream().filter(recordProcessor -> recordProcessor.supports(this.properties.getLogMode()))
                .findFirst().orElse(new StdoutRecordProcessor(this));
        return this.currentRecordProcessor;
    }

    /**
     * Get BeanFactory from Spring
     *
     * @return BeanFactory
     */
    public ConfigurableListableBeanFactory getBeanFactory() {
        return this.applicationContext.getBeanFactory();
    }

    /**
     * Get bean, using the default value if null
     *
     * @param clazz Class<T>
     * @return T
     */
    public <T> T getBeanOrDefault(Class<T> clazz, T defaultValue) {
        T obj = null;
        try {
            obj = this.applicationContext.getBean(clazz);
        } catch (Exception e) {
            if (defaultValue != null) {
                obj = defaultValue;
            } else {
                LoggerPrinter.warn(log, e.getMessage());
            }
        }
        return obj;
    }

    /**
     * Get bean, using the default value if null
     *
     * @param clazz Class<T>
     * @return T
     */
    public <T> T getBeanOrDefault(String name, Class<T> clazz, T defaultValue) {
        T obj = null;
        try {
            obj = this.applicationContext.getBean(name, clazz);
        } catch (Exception e) {
            if (defaultValue != null) {
                obj = defaultValue;
            } else {
                LoggerPrinter.debug(log, e.getMessage());
            }
        }
        return obj;
    }

    /**
     * Get bean for type
     *
     * @param clazz Class<T>
     * @return T
     */
    public <T> T getBean(Class<T> clazz) {
        return getBeanOrDefault(clazz, null);
    }

    /**
     * Get bean for name and type
     *
     * @param name String
     * @return T
     */
    public <T> T getBean(String name, Class<T> clazz) {
        return getBeanOrDefault(name, clazz, null);
    }

    /**
     * Get beans for type
     *
     * @param clazz Class<T>
     * @return T
     */
    public <T> Map<String, T> getBeans(Class<T> clazz) {
        Map<String, T> beansOfType = new HashMap<>();
        try {
            beansOfType = this.applicationContext.getBeansOfType(clazz);
        } catch (Exception e) {
            LoggerPrinter.info(log, e.getMessage());
        }
        return beansOfType;
    }

    /**
     * Get an existing IgnoreMethodProcessor or create a default
     *
     * @return {@link IgnoreMethodProcessor}
     */
    private IgnoreMethodProcessor generateIgnoreMethodProcessor() {
        IgnoreMethodProcessor ignoreMethodProcessor = getBeanOrDefault(IgnoreMethodProcessor.class, new DefaultIgnoreMethodProcessor());
        ignoreMethodProcessor.add(ignoreMethodProcessor.get());
        return ignoreMethodProcessor;
    }

    /**
     * Get an existing IgnoreUriProcessor or create a default
     *
     * @return {@link IgnoreUriProcessor}
     */
    private IgnoreUriProcessor generateIgnoreUriProcessor() {
        IgnoreUriProcessor ignoreUriProcessor = getBeanOrDefault(IgnoreUriProcessor.class, new DefaultIgnoreUriProcessor());
        ignoreUriProcessor.add(ignoreUriProcessor.get());
        return ignoreUriProcessor;
    }

    /**
     * Get an existing ResponseSuccessDefineProcessor or create a default
     *
     * @return {@link ResponseSuccessDefineProcessor}
     */
    private ResponseSuccessDefineProcessor generateResponseSuccessDefineProcessor() {
        ResponseSuccessDefineProcessor responseSuccessDefineProcessor = getBeanOrDefault(ResponseSuccessDefineProcessor.class, new DefaultResponseSuccessDefineProcessorProcessor());
        AgileLoggerSpringProperties.ResponseSuccessDefine userResponseSuccessDefine = this.properties.getResponseSuccessDefine();
        if (userResponseSuccessDefine != null) {
            responseSuccessDefineProcessor.setResponseSuccessDefine(userResponseSuccessDefine);
        }
        return responseSuccessDefineProcessor;
    }

    public ExecutorService generateExecutorService() {
        if (this.properties.isAsync()) {
            AgileLoggerSpringProperties.AsyncParams asyncParams = this.properties.getAsyncParams();
            int corePoolSize = asyncParams.getCorePoolSize();
            int maximumPoolSize = asyncParams.getMaximumPoolSize();
            if (corePoolSize > maximumPoolSize) {
                throw new InvalidDataException("CorePoolSize cannot be less than maximumPoolSize: %d < %d", corePoolSize, maximumPoolSize);
            }
            return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, asyncParams.getKeepAliveTime(), TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(asyncParams.getQueueSize()),
                    new BasicThreadFactory.Builder().namingPattern(asyncParams.getThreadNamePrefix() + "-%d").daemon(true).build(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
        return null;
    }

}
