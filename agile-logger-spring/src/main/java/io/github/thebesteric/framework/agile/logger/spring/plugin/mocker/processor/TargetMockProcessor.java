package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor;

import io.github.thebesteric.framework.agile.logger.commons.exception.DataNotExistsException;
import io.github.thebesteric.framework.agile.logger.commons.exception.HttpException;
import io.github.thebesteric.framework.agile.logger.commons.exception.InvalidDataException;
import io.github.thebesteric.framework.agile.logger.commons.utils.FileUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.HttpClient;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockCache;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.lang.reflect.Method;

/**
 * TargetMockProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public class TargetMockProcessor extends AbstractCachedMockProcessor {

    public static final String FILE_PROTOCOL = "file:";
    public static final String CLASSPATH_PROTOCOL = "classpath:";
    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTPS_PROTOCOL = "https://";

    private final HttpClient httpClient;

    public TargetMockProcessor(MockCache mockCache, HttpClient httpClient) {
        super(mockCache);
        this.httpClient = httpClient;
    }

    @Override
    public boolean match(Mocker mocker) {
        return StringUtils.isEmpty(mocker.value()) && StringUtils.isNotEmpty(mocker.target());
    }

    @Override
    public Object doProcess(Mocker mocker, Method method, Object[] args) throws Throwable {
        String target = mocker.target();
        String protocol = getTargetProtocol(target);
        Object result = null;
        switch (protocol) {
            case FILE_PROTOCOL:
                result = processFileTarget(target, method);
                break;
            case CLASSPATH_PROTOCOL:
                result = processClasspathTarget(target, method);
                break;
            case HTTP_PROTOCOL:
            case HTTPS_PROTOCOL:
                result = processRemoteTarget(target, method, args);
                break;
        }

        return result;
    }

    @Override
    public int order() {
        return MockOrderEnum.TARGET_ORDER.order();
    }

    private Object processRemoteTarget(String target, Method method, Object[] args) throws Exception {
        HttpClient.ResponseEntry responseEntry = httpClient.execute(target, method, args);
        if (responseEntry.getCode() == R.HttpStatus.OK.getCode()) {
            String body = responseEntry.getBody();
            return handleMockValue(body, method.getReturnType());
        }
        throw new HttpException("http request error: %s, code: %d", target, responseEntry.getCode());
    }

    private Object processFileTarget(String target, Method method) throws IOException {
        target = target.substring(FILE_PROTOCOL.length());
        File file = new File(target);
        if (file.exists()) {
            String mockValue = FileUtils.read(file);
            if (StringUtils.isNotEmpty(mockValue)) {
                return handleMockValue(mockValue, method.getReturnType());
            }
        }
        throw new DataNotExistsException("%s is not found in classpath", target);
    }

    private Object processClasspathTarget(String target, Method method) throws IOException {
        target = target.substring(CLASSPATH_PROTOCOL.length());
        Resource resource = new ClassPathResource(target);
        if (resource.exists()) {
            String mockValue = FileUtils.read(resource.getInputStream());
            if (StringUtils.isNotEmpty(mockValue)) {
                return handleMockValue(mockValue, method.getReturnType());
            }
        }
        throw new DataNotExistsException("%s is not found in local", target);
    }

    public String getTargetProtocol(String target) {
        String protocol = null;
        if (target.startsWith(FILE_PROTOCOL)) {
            protocol = FILE_PROTOCOL;
        } else if (target.startsWith(CLASSPATH_PROTOCOL)) {
            protocol = CLASSPATH_PROTOCOL;
        } else if (target.startsWith(HTTP_PROTOCOL)) {
            protocol = HTTP_PROTOCOL;
        } else if (target.startsWith(HTTPS_PROTOCOL)) {
            protocol = HTTPS_PROTOCOL;
        }
        if (protocol == null) {
            throw new InvalidDataException("Invalid target: %s", target);
        }
        return protocol;
    }


}
