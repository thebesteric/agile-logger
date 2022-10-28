package agile.logger.example.web;

import io.github.thebesteric.framework.agile.logger.boot.starter.annotation.EnableAgileLogger;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.RequestLoggerProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.record.CustomRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.request.MetricsRequestLoggerProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Application
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-16 15:35:23
 */
@SpringBootApplication(scanBasePackages = "agile.logger.example.web")
@EnableAgileLogger
@EnableFeignClients
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public RequestLoggerProcessor requestLoggerProcessor() {
        return new MetricsRequestLoggerProcessor();
    }

    @Bean
    public RecordProcessor recordProcessor(AgileLoggerContext agileLoggerContext) {
        return new CustomRecordProcessor(agileLoggerContext) {
            @Override
            public void doProcess(InvokeLog invokeLog) throws Throwable {
                if (invokeLog instanceof RequestLog) {
                    RequestLog requestLog = (RequestLog) invokeLog;
                    System.out.println("This is a request log: " + requestLog);
                } else {
                    System.out.println("This is a invoke log: " + invokeLog);
                }
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // @Bean
    // public InvokeLoggerProcessor invokeLoggerProcessor() {
    //     return new AbstractInvokeLoggerProcessor() {
    //         @Override
    //         public InvokeLog doAfterProcessor(InvokeLog invokeLog) {
    //             invokeLog.setExtra("something");
    //             return invokeLog;
    //         }
    //     };
    // }

    // @Bean
    // public IgnoreMethodProcessor ignoreMethodProcessor() {
    //     return new AbstractIgnoreMethodProcessor() {
    //         @Override
    //         public void addIgnoreMethods(Set<IgnoreMethod> ignoreMethods) {
    //             // ignoreMethods.add(IgnoreMethod.builder().methodName("^sayHe.*$").build());
    //             // ignoreMethods.add(IgnoreMethod.builder().clazz(TestService.class).methodName("login").build());
    //         }
    //     };
    // }

    // @Bean
    // public IgnoreUriProcessor ignoreUriProcessor() {
    //     return new AbstractIgnoreUriProcessor() {
    //         @Override
    //         public void addIgnoreUris(Set<String> ignoreUris) {
    //             ignoreUris.add("/test/hi");
    //         }
    //     };
    // }

}
