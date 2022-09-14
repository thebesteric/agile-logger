# agile-logger
A flexible logging plug-in
> 基于 Spring Boot 的一款全局日志记录插件

## Quick start
[Download By Maven Center](https://search.maven.org/search?q=g:io.github.thebesteric.framework.switchlogger)
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
@SwitchLogger(tag = "service")
public class TestService {

    @Autowired
    private TestAdapter testAdapter;

    public String sayHello(String name) {
        return testAdapter.sayHello(name, new Date());
    }

}

@Component
@SwitchLogger(tag = "adapter")
public class TestAdapter {
    
    public String sayHello(String name, Date date) {
        return "hello " + name + " at " + date.toLocaleString();
    }

}

// {"log_id":"bdbe1b5b-a3ed-48da-840e-79c72cf9502c","log_parent_id":"05e067df-57d6-4421-9034-49fcf997a0c9","tag":"adapter","level":"INFO","track_id":"ccd72925-1524-49bc-abee-263898131bcc","created_at":"2022-09-14 11:58:55","execute_info":{"class_name":"agile.logger.example.web.quickstart.TestAdapter","method_info":{"method_name":"sayHello","return_type":"java.lang.String","signatures":{"name":"java.lang.String","date":"java.util.Date"},"arguments":{"name":"张三","date":1663127935568}},"created_at":"2022-09-14 11:58:55","duration":4699},"result":"hello 张三 at 2022年9月14日 上午11:58:55","exception":null,"extra":null,"thread_name":"http-nio-8080-exec-2"}
// {"log_id":"05e067df-57d6-4421-9034-49fcf997a0c9","log_parent_id":"df94194e-656a-4f83-b5ad-2aced98e1c13","tag":"service","level":"INFO","track_id":"ccd72925-1524-49bc-abee-263898131bcc","created_at":"2022-09-14 11:58:55","execute_info":{"class_name":"agile.logger.example.web.quickstart.TestService","method_info":{"method_name":"sayHello","return_type":"java.lang.String","signatures":{"name":"java.lang.String"},"arguments":{"name":"张三"}},"created_at":"2022-09-14 11:58:55","duration":4709},"result":"hello 张三 at 2022年9月14日 上午11:58:55","exception":null,"extra":null,"thread_name":"http-nio-8080-exec-2"}
// {"log_id":"df94194e-656a-4f83-b5ad-2aced98e1c13","log_parent_id":null,"tag":"default","level":"INFO","track_id":"ccd72925-1524-49bc-abee-263898131bcc","created_at":"2022-09-14 11:58:55","execute_info":{"class_name":"agile.logger.example.web.quickstart.TestController","method_info":{"method_name":"sayHello","return_type":"io.github.thebesteric.framework.agile.logger.spring.domain.R","signatures":{"name":"java.lang.String"},"arguments":{}},"created_at":"2022-09-14 11:58:55","duration":4737},"result":{"code":200,"data":"hello 张三 at 2022年9月14日 上午11:58:55","timestamp":1663127940274,"message":"SUCCEED","trackId":"ccd72925-1524-49bc-abee-263898131bcc"},"exception":null,"extra":null,"thread_name":"http-nio-8080-exec-2","session_id":null,"uri":"/test","url":"http://127.0.0.1:8080/test?name=张三","method":"GET","protocol":"HTTP/1.1","ip":"127.0.0.1","domain":"http://127.0.0.1:8080","server_name":"127.0.0.1","local_addr":"127.0.0.1","local_port":8080,"remote_addr":"127.0.0.1","remote_port":58630,"query":"name=张三","cookies":[],"headers":{"sec-fetch-mode":"cors","sec-ch-ua":"\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"","sec-ch-ua-mobile":"?0","sec-fetch-site":"none","accept-language":"zh-CN,zh;q=0.9","sec-ch-ua-platform":"\"macOS\"","host":"127.0.0.1:8080","connection":"keep-alive","accept-encoding":"gzip, deflate, br","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36","accept":"*/*","sec-fetch-dest":"empty"},"params":{"name":"张三"},"body":null,"raw_body":null,"duration":4737,"response":{"status":200,"content_type":"application/json","locale":"zh_CN_#Hans","headers":{}},"metrics":{"total_request":2,"avg_response_time":2374,"min_response_time":12,"max_response_time":4737}}
```

## 日志格式
日志格式包括 **API 层日志** 格式和 **方法调用层** 日志格式两种
- API 层日志格式包括一些常见的 HTTP 请求和响应相关的信息，以及 API 执行信息，通常指：Controller 层
- 方法调用层（Service、Adapter 等）的日志格式包括方法执行的相关信息以及返回信息，通常指：非 Controller 层

### API 层日志格式
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
            "arguments": "方法签名对应的参数值 => Map<String, Object>",
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
    }
}
```
### 方法调用层日志格式
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
        "arguments": "方法签名对应的参数值 => Map<String, Object>",
      },
      "created_at": "方法执行开始时间 => String",
      "duration": "方法执行时间，单位：毫秒 => int"
    },
    "result": "方法返回值 => Object",
    "exception": "异常信息 => String",
    "extra": "扩展信息，可自定义 => String",
    "thread_name": "执行线程名 => String"
}
```
## 配置
### 全局开关配置
```yaml
sourceflag:
  agile-logger:
    enable: true # true or false，默认为: true
```
### 日志输出配置
log-mode 支持 stdout, log, cache, redis, database
- 输出到控制台
> log-mode: stdout
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: stdout
```
- 输出到日志文件 
> log-mode: log
```yaml
sourceflag:
  agile-logger:
    enable: true
    log-mode: log
```
- 输出到缓存
> log-mode: cache  
> 需要引入: agile-logger-plugin-cache
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.agile</groupId>
    <artifactId>agile-logger-plugin-cache</artifactId>
    <version>${project.version}</version>
</dependency>
```
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
- 输出到 Redis
> log-mode: redis  
> 需要引入: agile-logger-plugin-redis
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.agile</groupId>
    <artifactId>agile-logger-plugin-redis</artifactId>
    <version>${project.version}</version>
</dependency>
```
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
- 输出到 MySQL
> log-mode: database  
> 需要引入: agile-logger-plugin-database
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.agile</groupId>
    <artifactId>agile-logger-plugin-database</artifactId>
    <version>${project.version}</version>
</dependency>
```
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


```java
@Bean
public IdGenerator idGenerator() { // 名字必须是：idGenerator
    return () -> "1";
}

@Bean
public IdGenerator trackIdGenerator() { // 名字必须是：trackIdGenerator
    return () -> "2";
}
```