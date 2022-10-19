# agile-logger
A flexible logging plug-in
> 基于 Spring Boot 的一款全局日志记录插件

## 1. Quick start
[Download By Maven Center](https://search.maven.org/search?q=io.github.thebesteric.framework.agile)
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.agile</groupId>
    <artifactId>agile-logger-boot-starter</artifactId>
    <version>${latest.version}</version>
</dependency>
```

在启动类注解上标记`@EnableAgileLogger`即可
```java
@EnableAgileLogger
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
添加上`@EnableAgileLogger`后，即可对所有 Controller 层按照默认规则进行全局控制层日志记录

---
### 记录更多的日志
AgileLogger 除了会自动记录 Controller 层的日志，还可以记录其他任何调用链上的日志，只需要分别在类上或方法上添加`@AgileLogger`注解，就可以来记录方法执行日志
> 如：当 Controller 层调用 Service 层，再由 Service 层调用 Adapter 层的话，就会串联为一个完整的**日志跟踪链**
```java
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping
    public R sayHello(@RequestParam String name) {
        String wording = testService.sayHello(name);
        return R.success().setData(wording);
    }

}

@Component
@AgileLogger(tag = "service")
public class TestService {

    @Autowired
    private TestAdapter testAdapter;

    public String sayHello(String name) {
        return testAdapter.sayHello(name, new Date());
    }

}

@Component
@AgileLogger(tag = "adapter")
public class TestAdapter {
    
    public String sayHello(String name, Date date) {
        return "hello " + name + " at " + date.toLocaleString();
    }

}

// {"log_id":"bdbe1b5b-a3ed-48da-840e-79c72cf9502c","log_parent_id":"05e067df-57d6-4421-9034-49fcf997a0c9","tag":"adapter","level":"INFO","track_id":"ccd72925-1524-49bc-abee-263898131bcc","created_at":"2022-09-14 11:58:55","execute_info":{"class_name":"agile.logger.example.web.quickstart.TestAdapter","method_info":{"method_name":"sayHello","return_type":"java.lang.String","signatures":{"name":"java.lang.String","date":"java.util.Date"},"arguments":{"name":"张三","date":1663127935568}},"created_at":"2022-09-14 11:58:55","duration":4699},"result":"hello 张三 at 2022年9月14日 上午11:58:55","exception":null,"extra":null,"thread_name":"http-nio-8080-exec-2"}
// {"log_id":"05e067df-57d6-4421-9034-49fcf997a0c9","log_parent_id":"df94194e-656a-4f83-b5ad-2aced98e1c13","tag":"service","level":"INFO","track_id":"ccd72925-1524-49bc-abee-263898131bcc","created_at":"2022-09-14 11:58:55","execute_info":{"class_name":"agile.logger.example.web.quickstart.TestService","method_info":{"method_name":"sayHello","return_type":"java.lang.String","signatures":{"name":"java.lang.String"},"arguments":{"name":"张三"}},"created_at":"2022-09-14 11:58:55","duration":4709},"result":"hello 张三 at 2022年9月14日 上午11:58:55","exception":null,"extra":null,"thread_name":"http-nio-8080-exec-2"}
// {"log_id":"df94194e-656a-4f83-b5ad-2aced98e1c13","log_parent_id":null,"tag":"default","level":"INFO","track_id":"ccd72925-1524-49bc-abee-263898131bcc","created_at":"2022-09-14 11:58:55","execute_info":{"class_name":"agile.logger.example.web.quickstart.TestController","method_info":{"method_name":"sayHello","return_type":"io.github.thebesteric.framework.agile.logger.spring.domain.R","signatures":{"name":"java.lang.String"},"arguments":{}},"created_at":"2022-09-14 11:58:55","duration":4737},"result":{"code":200,"data":"hello 张三 at 2022年9月14日 上午11:58:55","timestamp":1663127940274,"message":"SUCCEED","trackId":"ccd72925-1524-49bc-abee-263898131bcc"},"exception":null,"extra":null,"thread_name":"http-nio-8080-exec-2","session_id":null,"uri":"/test","url":"http://127.0.0.1:8080/test?name=张三","method":"GET","protocol":"HTTP/1.1","ip":"127.0.0.1","domain":"http://127.0.0.1:8080","server_name":"127.0.0.1","local_addr":"127.0.0.1","local_port":8080,"remote_addr":"127.0.0.1","remote_port":58630,"query":"name=张三","cookies":[],"headers":{"sec-fetch-mode":"cors","sec-ch-ua":"\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"","sec-ch-ua-mobile":"?0","sec-fetch-site":"none","accept-language":"zh-CN,zh;q=0.9","sec-ch-ua-platform":"\"macOS\"","host":"127.0.0.1:8080","connection":"keep-alive","accept-encoding":"gzip, deflate, br","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36","accept":"*/*","sec-fetch-dest":"empty"},"params":{"name":"张三"},"body":null,"raw_body":null,"duration":4737,"response":{"status":200,"content_type":"application/json","locale":"zh_CN_#Hans","headers":{}},"metrics":{"total_request":2,"avg_response_time":2374,"min_response_time":12,"max_response_time":4737}}
```

## 2. 日志格式
日志格式包括 **API 层日志** 格式和 **方法调用层** 日志格式两种
- API 层日志格式包括一些常见的 HTTP 请求和响应相关的信息，以及 API 执行信息，通常指：Controller 层
- 方法调用层（Service、Adapter 等）的日志格式包括方法执行的相关信息以及返回信息，通常指：非 Controller 层

### 2.1 API 层日志格式
```json
{
    "log_id": "日志 ID => String",
    "log_parent_id": "父日志 ID，用于查看调用顺序 => String",
    "tag": "标签，可自定义 => String",
    "level": "日志级别 => String",
    "track_id": "日志链追踪 ID => String",
    "created_at": "方法执行开始时间 => String",
    "execute_info": {
        "class_name": "类名 => String",
        "method_info": {
            "method_name": "方法名 => String",
            "return_type": "返回值类型 => String",
            "signatures": "方法签名 => Map<String, Object>",
            "arguments": "方法签名对应的参数值 => Map<String, Object>"
        },
        "created_at": "方法执行开始时间 => String",
        "duration": "方法执行时间，单位：毫秒 => int"
    },
    "result": "方法返回值 => Object",
    "exception": "异常信息 => String",
    "extra": "扩展信息，可自定义 => String",
    "thread_name": "执行线程名 => String",
    "session_id": "Session ID => String",
    "uri": "URI => String",
    "url": "URL => String",
    "method": "请求方式 => String",
    "protocol": "HTTP 协议 => String",
    "ip": "IP 地址 => String",
    "domain": "请求域名 => String",
    "server_name": "服务名 => String",
    "local_addr": "本地地址 => String",
    "local_port": "本地端口 => int",
    "remote_addr": "远程地址 => String",
    "remote_port": "远程端口 => int",
    "query": "URL 请求参数 => String",
    "cookies": "Cookie 信息 => Set",
    "headers": "请求头信息 => Map<String, String>",
    "params": "请求参数 => Map<String, String>",
    "body": "请求体 => Object",
    "raw_body": "请求体（未格式化） => String",
    "duration": "方法持续时间 => long",
    "response": {
        "status": "HTTP 响应状态 => int",
        "content_type": "响应内容类型 => String",
        "locale": "语言 => String",
        "headers": "响应头信息 => Map<String, String>"
    },
    "mock": "是否是模拟数据 => boolean"
}
```
### 2.2 方法调用层日志格式
```json
{
    "log_id": "日志 ID => String",
    "log_parent_id": "父日志 ID，用于查看调用顺序 => String",
    "tag": "标签，可自定义 => String",
    "level": "日志级别 => String",
    "track_id": "日志链追踪 ID => String",
    "created_at": "方法执行开始时间 => String",
    "execute_info": {
      "class_name": "类名 => String",
      "method_info": {
        "method_name": "方法名 => String",
        "return_type": "返回值类型 => String",
        "signatures": "方法签名 => Map<String, Object>",
        "arguments": "方法签名对应的参数值 => Map<String, Object>"
      },
      "created_at": "方法执行开始时间 => String",
      "duration": "方法执行时间，单位：毫秒 => int"
    },
    "result": "方法返回值 => Object",
    "exception": "异常信息 => String",
    "extra": "扩展信息，可自定义 => String",
    "thread_name": "执行线程名 => String",
    "mock": "是否是模拟数据 => boolean"
}
```

## 3. Yaml 配置
### 3.1 全局开关配置
**_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true # true or false，默认为: true
```
### 3.2 日志输出配置
log-mode 支持 stdout, log, cache, redis, database
#### 输出到控制台
> log-mode: stdout

- **_pom.xml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: stdout
```
#### 输出到日志文件 
> log-mode: log

- **_pom.xml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: log
```
#### 输出到缓存
> **_log-mode: cache_**  
> 需要引入: **agile-logger-plugin-cache** 插件  

- **_pom.xml 配置_**
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.agile</groupId>
    <artifactId>agile-logger-plugin-cache</artifactId>
    <version>${project.version}</version>
</dependency>
```
- **_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: cache
    plugins:
      cache:
        initial-capacity: 200 # 初始容量
        maximum-size: 2000 # 最大容量
        expired-time: 3600000 # 过期时间，单位：毫秒
```
#### 输出到 Redis
> **_log-mode: redis_**  
> 需要引入: **agile-logger-plugin-redis** 插件    

- **_pom.xml 配置_**
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.agile</groupId>
    <artifactId>agile-logger-plugin-redis</artifactId>
    <version>${project.version}</version>
</dependency>
```
- **_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: redis
    plugins:
      redis:
        host: 127.0.0.1
        port: 6379
        database: 1
        expired-time: 3600000
        username: root
        password: root
```
#### 输出到 MySQL
> **_log-mode: database_**  
> 需要引入: **agile-logger-plugin-database** 插件  
> 需要引入对应的数据库 Jar 包，如：**mysql-connector-java** 包  

- **_pom.xml 配置_**
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.agile</groupId>
    <artifactId>agile-logger-plugin-database</artifactId>
    <version>${project.version}</version>
</dependency>
```
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.30</version>
</dependency>
```
- **_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: database
    plugins:
      database:
        url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.cj.jdbc.Driver
        username: root
        password: root
```
### 3.3 RPC 访问配置
#### 3.4.1 Feign Client 配置
提供记录 feign 的访问日志
> _**rpc.feign.enable: true**_  
> 需要引入: **agile-logger-rpc-feign** 插件
- **_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: stdout
    rpc:
      feign:
        enable: true
```
### 3.4 URL 过滤器
> **_includes_**: 配置需要拦截的 url 正则表达式  
> **_excludes_**: 配置不需要拦截的 url 正则表达式  
> _PS: excludes 的优先级大于 includes_

**_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: stdout
    url-filter:
      includes: [".*"]
      excludes: ["/favicon.ico", "/check/health/.*"]
```
### 3.5 同步/异步日志配置
> 默认使用异步方式记录日志（推荐）

**_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: stdout
    async: 
      enable: true # 开启异步，默认：true
      async-params:
        core-pool-size: 1 # 核心线程数量
        maximum-pool-size: 8 # 最大线程数量，默认：CPU 核心数
        queue-size: 1024 # 队列大小，默认：1024
        keep-alive-time: 60000 # 线程空闲时间，单位：毫秒
```
### 3.6 使用 sky-walking traceId 替代默认 tackId
> 开启 sky-walking traceId 的时候，需要注意是否连接上 OAP Server  
> 如：-javaagent:/agent/skywalking-agent.jar -Dskywalking.agent.service_name=app -Dskywalking.collector.backend_service=127.0.0.1:11800  
> 若系统未监测到 sky-walking 服务的时候，依然会使用默认的 trackId

**_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    use-sky-walking-trace: true # 开启，默认：false
```
### 3.7 全局成功响应字段配置
通常用于访问上游，或给下游返回数据时，对应的的成功 code 码，以及对应的消息字段，如果 code 对应的成功响应值不符，日志会以异常的形式记录对应的 message 信息
> code-fields: 定义全局正常的返回 code 字段与值，支持定义多组  
> message-fields: 定义返回内容信息的字段名称

**_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: stdout
    response-success-define:
      code-fields:
        - name: code
          value: 200
        - name: code
          value: 100
      message-fields: message, msg
```
### 3.8 自定义 trackId 的名称
> 系统内置支持："track-id", "x-track-id", "trans-id", "x-trans-id", "trace-id", "x-trace-id", "transaction-id", "x-transaction-id" 字段作为默认 trackId 名称
> 如应用有其他的名称，支持在请求头中指定，指定后宽家会使用请求头中的值作为 trackId 的值
```yaml
sourceflag:
  agile-logger:
    config:
      track-id-name: my-track-id
```
### <span id="3.9">3.9 自定义 version 的名称</span>
> 系统内置支持："version", "x-version", "app-version", "x-app-version" 字段作为默认版本号名称  
> 如应用有其他的名称作为版本标识，支持在 yaml 文件中自定义  
> PS: 版本号必须包含在请求头中，可以使用 `VersionUtils.get()` 获取当前版本号
```yaml
sourceflag:
  agile-logger:
    config:
      version-name: my-version
```

## 4. 注解配置
### 4.1 @EnableAgileLogger 注解
> 作用在 SpringBoot 的启动类上，表示开始 AgileLogger 日志框架;  
> 注意：若 yaml 中 sourceflag.agile-logger.enable 设置为 false，@EnableAgileLogger 也不会生效  
#### 示例
```java
@SpringBootApplication(scanBasePackages = "agile.logger.example.web")
@EnableAgileLogger
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
```
### 4.2 @AgileLogger 注解
> `@AgileLogger` 可以作用在类或方法上;  
> 作用在类上，表示类下所有的非 `private` 或 `static` 修饰的方法都将被日志所监控;  
> 作用在方法上，则方法上的 `@AgileLogger` 中所有的属性（**_除 `ignoreMethods` 外_**）会覆盖作用在类上的 `@AgileLogger` 的属性
> 注意: 如果 `@AgileLogger` 作用在方法上，且类上没有 `@AgileLogger` 注解，则只有改方法会被日志监控，其他方法均不会被监控

#### 相关属性
- `tag`: 用于标识当前类或方法的层级，可以设置如：service、adapter、API、UPSTREAM 等
- `level`: 日志级别，可以设置：DEBUG、INFO（默认级别）、WARN、ERROR，如果方法执行过程中出现异常，会自动变成 ERROR 级别
- `extra`: 扩展信息，通常应用在方法上的 @AgileLogger，可以用来记录一些额外信息
- `ignoreMethods`: 设置需要忽略的方法名称，支持正则，不支持方法重载

#### 示例
```java
@Service
@AgileLogger(tag = "service", ignoreMethods = {"^foo.*"})
public class TestService {
    @Autowired
    private TestAdapter testAdapter;

    @AgileLogger(extra = "just say hello", level = AbstractEntity.LEVEL_DEBUG)
    public String sayHello(String name) {
        name = foo(name);
        return testAdapter.sayHello(name, new Date());
    }

    public String sayHi(String name) {
        name = foo(name);
        return testAdapter.sayHi(name, new Date());
    }

    // @IgnoreMethod
    public String foo(String name) {
        return name;
    }
}
```
### 4.3 @IgnoreMethods 注解
> 作用在类上，同 `@AgileLogger` 的 `ignoreMethods` 属性  
> 如果类上同时定义了 `@AgileLogger(ignoreMethods = {"^foo.*"})` 和 `@IgnoreMethods({"^bar.*"})` 则会进行合并  
> 注意：`@IgnoreMethods` 同样不支持方法重载，如需要具体的某个方法进行忽略，可以使用 `@IgnoreMethod` 注解
#### 相关属性
- `value`: 设置需要忽略的方法名称，支持正则
#### 示例
```java
@Service
@AgileLogger(tag = "service", ignoreMethods = {"^foo.*"})
@IgnoreMethods({"^bar.*"})
public class TestService {
    @Autowired
    private TestAdapter testAdapter;

    @AgileLogger(extra = "just say hello", level = AbstractEntity.LEVEL_DEBUG)
    public String sayHello(String name) {
        name = foo(name);
        return testAdapter.sayHello(name, new Date());
    }

    public String sayHi(String name) {
        name = foo(name);
        return testAdapter.sayHi(name, new Date());
    }
    
    public String foo(String name) {
        return name;
    }
}
```
### 4.4 @IgnoreMethod 注解
> 作用在方法上，表示需要忽略的方法

```java
@Service
@AgileLogger(tag = "service")
public class TestService {
    @Autowired
    private TestAdapter testAdapter;
    
    public String sayHello(String name) {
        name = foo(name);
        return testAdapter.sayHello(name, new Date());
    }
    
    @IgnoreMethod
    public String sayHi(String name) {
        name = foo(name);
        return testAdapter.sayHi(name, new Date());
    }
    
    public String foo(String name) {
        return name;
    }
}
```

## 5. 扩展
### 5.1 自定义 ID 生成器
#### 5.1.1 自定义 LOG_ID 生成器
> 方法名必须是`idGenerator`
```java
@Configuration
public class AppConfiguration {
    // 名字必须是: idGenerator
    @Bean
    public IdGenerator idGenerator() {
        return () -> UUID.randomUUID().toString();
    }
}
```
#### 5.1.2 自定义 TRACK_ID 生成器
> 方法名必须是`trackIdGenerator`
```java
@Configuration
public class AppConfiguration {
    // 名字必须是: trackIdGenerator
    @Bean
    public IdGenerator trackIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }
}
```
### 5.2 自定义全局忽略器
#### 5.2.1 自定义全局 url 忽略器
> 指定需要被忽略的 url（不会被记录日志），支持正则表达式；

> 推荐继承`AbstractIgnoreUriProcessor`抽象类，重写`addIgnoreUris`方法;  
> `AbstractIgnoreUriProcessor` 会自动忽略 `/favicon.ico` 这个 uri
```java
@Configuration
public class AppConfiguration {
    @Bean
    public AbstractIgnoreUriProcessor ignoreUriProcessor() {
        return new AbstractIgnoreUriProcessor() {
            @Override
            public void addIgnoreUris(Set<String> ignoreUris) {
                ignoreUris.add("/test/hi");
                ignoreUris.add("/check/health/.*");
            }
        };
    }
}
```
#### 5.2.2 自定义全局方法忽略器
> 指定需要被忽略的方法（不会被记录日志），支持正则表达式；  
> `ignoreMethod.className(String className)`: 类名，支持正则表达式，非必须；  
> `ignoreMethod.methodName(String methodName)`: 方法名，支持正则表达式，必须；  
> 若 className 不指定，默认为匹配所有类，相当于：`ignoreMethod.className(".*")`  

> 推荐继承 `AbstractIgnoreMethodProcessor` 抽象类，重写`addIgnoreMethods`方法;  
> `AbstractIgnoreMethodProcessor` 会自动忽略 `equals`,`toString`,`hashCode` 三个方法
```java
@Configuration
public class AppConfiguration {
    @Bean
    public IgnoreMethodProcessor ignoreMethodProcessor() {
        return new AbstractIgnoreMethodProcessor() {
            @Override
            public void addIgnoreMethods(Set<IgnoreMethod> ignoreMethods) {
                ignoreMethods.add(IgnoreMethod.builder().methodName("^get.*$").build());
                ignoreMethods.add(IgnoreMethod.builder().methodName("^set.*$").build());
            }
        };
    }
}
```
### 5.3 日志拦截器
#### 5.3.1 请求日志拦截器
> 请求日志指: Controller 层的日志  
> 所有请求日志均会被 `RequestLoggerProcessor` 所拦截  
> 实现 `RequestLoggerProcessor` 接口，重写 `processor` 方法  
> 推荐继承：`AbstractRequestLoggerProcessor` 类，重写 `doAfterProcessor(RequestLog requestLog)` 方法，重写日志中的内容  
```java
@Configuration
public class AppConfiguration {
    @Bean
    public RequestLoggerProcessor requestLoggerProcessor1() {
        return new AbstractRequestLoggerProcessor() {
            @Override
            public RequestLog doAfterProcessor(RequestLog requestLog) {
                requestLog.setExtra("something");
                return requestLog;
            }
        };
    }
}
```
> PS: 框架提供了一个 `MetricsRequestLoggerProcessor` 的 `RequestLoggerProcessor` 的实现类  
> 可以用于统计接口调用情况，包括：调用次数、平均响应时间、最小响应时间、最大响应时间、最小响应TrackId、最大响应TrackId
```java
@Configuration
public class AppConfiguration {
    @Bean
    public RequestLoggerProcessor requestLoggerProcessor() {
        return new MetricsRequestLoggerProcessor();
    }
}
```
添加后，日志后会增加 `metrics` 属性
```json
{
  "metrics": {
    "total_request": 15,
    "avg_response_time": 59,
    "min_response_time": 29,
    "max_response_time": 126,
    "min_response_track_id": "220f9b1b-0bd9-4e9f-b53f-b57f7ea131ba",
    "max_response_track_id": "7310353b-5d5d-45de-bff7-5dc1e459bf1b"
  }
}
```
#### 5.3.2 调用日志拦截器
> 调用日志指: Controller 层调用其他层的日志信息  
> 所有调用层的日志均会被 `InvokeLoggerProcessor` 所拦截  
> 实现 `InvokeLoggerProcessor` 接口，重写 `processor` 方法  
> 推荐继承：`AbstractInvokeLoggerProcessor` 类，重写 `doAfterProcessor(InvokeLog invokeLog)` 方法，重写日志中的内容  
```java
@Configuration
public class AppConfiguration {
    @Bean
    public InvokeLoggerProcessor invokeLoggerProcessor() {
        return new AbstractInvokeLoggerProcessor() {
            @Override
            public InvokeLog doAfterProcessor(InvokeLog invokeLog) {
                invokeLog.setExtra("something");
                return invokeLog;
            }
        };
    }
}
```
### 5.4 自定义日志记录器
> 支持用户自定义日志记录器，将日志输出到指定位置或修改（过滤）日志内容（如：混淆敏感信息）  
> 配置文件中的 `log-mode` 设置为 `custom`  
> 继承 `CustomRecordProcessor` 类，实现 `doProcess(InvokeLog invokeLog)` 方法  
> PS: 可以通过 `InvokeLog instanceof RequestLogger` 判断是否是**请求日志**还是**调用日志**  

**_application.yaml 配置_**
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: custom
```
**_示例_**
```java
@Configuration
public class AppConfiguration {
    @Bean
    public RecordProcessor recordProcessor(AgileLoggerContext agileLoggerContext) {
        return new CustomRecordProcessor(agileLoggerContext) {
            @Override
            public void doProcess(InvokeLog invokeLog) throws Throwable {
                if (invokeLog instanceof RequestLog) {
                    RequestLog requestLog = (RequestLog) invokeLog;
                    System.out.println("This is a Request-Log: " + requestLog);
                } else {
                    System.out.println("This is a Invoke-Log: " + invokeLog);
                }
            }
        };
    }
}
```
## 6. 其他
### 6.1 版本控制
> 方法上使用 `@Versioner` 注解，可以对请求参数或响应结果进行变更  
> 适用于：通过版本判断，根据不通的版本处理不通的请求或响应  
> `@Versioner` 中的 type 指定的的类必须实现 `VersionerAdapter<V, R>` 或继承 `AbstractVersionerAdapter<V, R>`  
> 其中 `V`: 代表需要进行版本控制的请求参数，`R`: 代表方法的返回值类型  
> 实现或重写 `public void request(V v)`: 当方法执行之前，回调用该方法，传入：实际的请求参数  
> 实现或重写 `Object response(R result)`: 当方法返回结果之前，回调用该方法，传入：当前方法的返回值  
> 如果只关注入参，可以继承 `RequestVersionerAdapter<V>` 重写 `public void request(V v)` 方法    
> 如果只关注出参，可以继承 `ResponseVersionerAdapter<R>` 重写 `public R response(R result)` 方法
```java
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Versioner(type = LoginVersion.class)
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = loginService.login(identity);
        return userInfo;
    }
}

public class LoginVersion extends AbstractVersionerAdapter<Identity, UserInfo> {
    @Override
    public void request(Identity identity) {
        if (VersionUtils.compareLessThan(VersionUtils.get(), "9.1.0")) {
            identity.setIdentity("customer");
        } else {
            identity.setIdentity("vip");
        }
    }

    @Override
    public Object response(UserInfo userInfo) {
        userInfo.setPassword("******");
        return result;
    }
}
```
#### 6.1.1 版本控制工具类
> 提供 `VersionUtils` 在版本控制方面提供相关 API 支持
- `VersionUtils.get()`: 获取当前请求的版本号，具体配置参考: [3.9 自定义 version 的名称](#3.9)
- `VersionUtils.compare(String appVersion, String compareVersion)`: 版本比较
  - appVersion > compareVersion: 返回 1
  - appVersion = compareVersion: 返回 0
  - appVersion < compareVersion: 返回 -1
- `int VersionUtils.compareEqual(String appVersion, String compareVersion)`: 相等返回 true
- `boolean VersionUtils.compareGreaterThan(String appVersion, String compareVersion)`: appVersion > compareVersion 返回 true
- `boolean VersionUtils.compareGreaterThanOrEqual(String appVersion, String compareVersion)`: appVersion >= compareVersion 返回 true
- `boolean VersionUtils.compareLessThan(String appVersion, String compareVersion)`: appVersion < compareVersion 返回 true
- `boolean VersionUtils.compareLessThanOrEqual(String appVersion, String compareVersion)`: appVersion <= compareVersion 返回 true

### 6.2 Mock 数据
> 方法上使用 `@Mocker` 注解，可以对响应结果进行变更  
> 适用于：测试或未上线前进行模拟返回  
> `enable`: 全局开关，默认开启，设置为 false 后，所有 @Mocker 均不生效  
> `expire-after-write`: 缓存设置，最后一次写操作后经过指定时间过期，默认 600s  
> `expire-after-access`: 缓存设置，最后一次读或写操作后经过指定时间过期，默认 600s  
> PS: 所有 mock 数据均存在缓存，可根据实际情况设置缓存时间  
```yaml
sourceflag:
  agile-logger:
    config:
      mock:
        enable: true # 全局开关，默认开启
        expire-after-write: 600 # 最后一次写操作后经过指定时间过期
        expire-after-access: 600 # 最后一次读或写操作后经过指定时间过期
```
#### 6.2.1 @Mocker 的 enable 属性
> 用于控制当前方法上的 `@Mocker` 是否生效，默认：true  
```java
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Mocker(value = "{username: lisi, password: 1234, greeting: hello}", enable = true)
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = loginService.login(identity);
        return userInfo;
    }
}
```
#### 6.2.2 @Mocker 的 value 属性
> `@Mocker(value="{username: xxx, password: xxx}")`: 直接利用 value 值提供 mock 数据  
> 适用于简单的 mock 数据，优先级最高  
```java
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Mocker("{username: lisi, password: 1234}")
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = loginService.login(identity);
        return userInfo;
    }
}
```
#### 6.2.3 @Mocker 的 type 属性
> `@Mocker(type="xxx.class")`: 编码方式产生 mock 数据
> 读取本地文件或网络数据，都设置的情况下，优先级低于 value 属性  
> `@Mocker` 中的 type 指定的的类必须实现 `MockAdapter<R>` 接口或继承 `AbstractMockerAdapter<R>`  
> 其中 `R`: 代表方法的返回值类型
```java
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Mocker(type = LoginMockAdapter.class)
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = loginService.login(identity);
        return userInfo;
    }
}

public class LoginMockAdapter implements MockerAdapter<UserInfo> {
  @Override
  public UserInfo mock() {
    return new UserInfo("lucy", "9988", "vip");
  }
}
```
> 如果期望一个类实现多个方法的 mock，可以继承 `MethodsMockerAdapter`, mock 数据的方法名必须与实际的方法名称一致，没有任何方法参数
```java
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private LoginService loginService;
  
    @Mocker(type = MultiMethodMockAdapter.class)
    @PostMapping("/mock1")
    public UserInfo mock1(@RequestBody Identity identity) {
      return loginService.login(identity);
    }

    @Mocker(type = MultiMethodMockAdapter.class)
    @PostMapping("/mock2")
    public R mock2(@RequestBody Identity identity) {
      return R.success(loginService.login(identity));
    }
}

public class MultiMethodMockAdapter extends MethodsMockerAdapter {

  public UserInfo mock1() {
    return new UserInfo("mock1", "***", "hello mock1");
  }

  public R mock2() {
    return R.success(new UserInfo("mock2", "***", "hello mock2"));
  }

}
```

#### 6.2.4 @Mocker 的 target 属性
> `@Mocker(target="xxx")`: 读取文件或远程地址的 mock 数据  
> target 表示读取本地文件或网络数据，都设置的情况下，优先级低于 value 和 type 属性 

##### 6.2.4.1 target 的 classpath 关键字
> `@Mocker(target = "classpath:/mock/userInfo.json")`: 读取类路径下的某个文件
```java
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Mocker(target = "classpath:/mock/userInfo.json")
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = loginService.login(identity);
        return userInfo;
    }
}
```

##### 6.2.4.2 target 的 file 关键字
> `@Mocker(target = "file:/Users/eric/demo/userInfo.json")`: 读取文件系统路径下的某个文件
```java
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Mocker(target = "file:/Users/eric/demo/userInfo.json")
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = loginService.login(identity);
        return userInfo;
    }
}
```

##### 6.2.4.3 target 的 http(s) 关键字
> `@Mocker(target = "https://xxxx/mock/userInfo")`: 读取网络的某个接口
> 默认会使用系统自带的 DefaultHttpClient 客户端，发送 GET 请求 target 的 URL 地址  
> 如想自定义请求规则，可实现 `HttpClient` 接口，实现 `execute(String url, Method method, Object[] args)` 方法
```java
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Mocker(target = "https://xxxx/mock/userInfo")
    public UserInfo login(@RequestBody Identity identity) {
        UserInfo userInfo = loginService.login(identity);
        return userInfo;
    }
}

@Configuration
public class AppConfiguration {
    @Bean
    public HttpClient httpClient() {
        return new HttpClient() {
            @Override
            public ResponseEntry execute(String url, Method method, Object[] args) throws Exception {
                // send http request...
                return new ResponseEntry(200, content);
            }
        };
    }
}
```