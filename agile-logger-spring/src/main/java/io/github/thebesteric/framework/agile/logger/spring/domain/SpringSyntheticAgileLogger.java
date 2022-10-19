package io.github.thebesteric.framework.agile.logger.spring.domain;

import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.SignatureUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SpringSyntheticAgileLogger
 *
 * @author Eric Joe
 * @version 1.0
 */
public class SpringSyntheticAgileLogger extends SyntheticAgileLogger {

    public static final String TAG_CONTROLLER = "controller";
    public static final String TAG_SERVICE = "service";
    public static final String TAG_REPOSITORY = "repository";
    public static final String TAG_COMPONENT = "component";

    public static Map<String, SpringSyntheticAgileLogger> cache = new HashMap<>(128);

    private SpringSyntheticAgileLogger(Method method) {
        super(method);
        setComponentTag(method.getDeclaringClass());
    }

    public static SpringSyntheticAgileLogger getSpringSyntheticAgileLogger(Method method) {
        String key = SignatureUtils.methodSignature(method);
        SpringSyntheticAgileLogger cachedSyntheticAgileLogger = cache.get(key);
        if (cachedSyntheticAgileLogger == null || Objects.equals(cachedSyntheticAgileLogger.level, AbstractEntity.LEVEL_ERROR)) {
            synchronized (SpringSyntheticAgileLogger.class) {
                cachedSyntheticAgileLogger = new SpringSyntheticAgileLogger(method);
                cache.put(key, cachedSyntheticAgileLogger);
            }
        }
        return cachedSyntheticAgileLogger;
    }

    private void setComponentTag(Class<?> type) {
        if (this.tag == null || this.tag.equals(AbstractEntity.TAG_DEFAULT)) {
            if (ReflectUtils.anyAnnotationPresent(type, RestController.class, Controller.class)) {
                this.tag = TAG_CONTROLLER;
            } else if (ReflectUtils.isAnnotationPresent(type, Service.class)) {
                this.tag = TAG_SERVICE;
            } else if (ReflectUtils.isAnnotationPresent(type, Repository.class)) {
                this.tag = TAG_REPOSITORY;
            } else if (ReflectUtils.isAnnotationPresent(type, Component.class)) {
                this.tag = TAG_COMPONENT;
            } else {
                this.tag = AbstractEntity.TAG_DEFAULT;
            }
        }
    }
}
