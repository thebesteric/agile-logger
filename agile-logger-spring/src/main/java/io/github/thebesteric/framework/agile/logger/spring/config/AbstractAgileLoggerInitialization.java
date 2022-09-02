package io.github.thebesteric.framework.agile.logger.spring.config;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathScanner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * AbstractAgileLoggerInitialization
 *
 * @author Eric Joe
 * @build 2022-08-16 14:32:23
 * @since 1.0
 */
public abstract class AbstractAgileLoggerInitialization implements SmartLifecycle, ApplicationContextAware {

    public static final String ID_GENERATOR_BEAN_NAME = "idGenerator";
    public static final String TRACK_ID_GENERATOR_BEAN_NAME = "trackIdGenerator";
    protected boolean isRunning = false;
    protected GenericApplicationContext applicationContext;

    protected final AgileLoggerSpringProperties properties;
    protected final List<ClassPathScanner> classPathScanners;

    public AbstractAgileLoggerInitialization(AgileLoggerSpringProperties properties, List<ClassPathScanner> classPathScanners) {
        this.properties = properties;
        this.classPathScanners = classPathScanners;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (GenericApplicationContext) applicationContext;
    }

    protected <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception ex) {
            return null;
        }
    }

    protected <T> T getBean(String name, Class<T> clazz) {
        return getBeanOrDefault(name, clazz, null);
    }

    protected <T> T getBeanOrDefault(String name, Class<T> clazz, T defaultValue) {
        try {
            return applicationContext.getBean(name, clazz);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
