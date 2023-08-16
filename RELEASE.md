## Version Change Log
### v1.0.0
- The first release
### v1.0.1
- Fixed: An issue where CGLIB classes could not be enhancing
### v1.0.2
- Fixed: An issue when VersionerAdapter impl is not override the request or response method
- Perf: `VersionerAdapter` support get currently method
- Perf: Optimized the `SignatureUtils.methodSignature` calculation mode
### v1.0.3
- Fixed: Repair `ReflectUtils.getActualTypeArguments` to find the problem of incomplete
- Perf: Optimize @Mocker execution timing
- Perf: RequestLog default tag name is "default", You can use RequestLogProcessor to change it
- Style: `VersionAdapter` rename to `VersionerAdapter`, `AbstractVersionAdapter` rename to `AbstractVersionerAdapter`
- Feature: `MetricsRequestLoggerProcessor` support `maxResponseTrackId` and `minResponseTrackId`
### v1.0.4
- Fixed: An issue when enhance where properties could not be injected
- Perf: Optimize `@Mocker` target http/https mode add method and args parameters when used custom `HttpClient`
### v1.0.5
- Refactor: Async config parameters adjustment
- Fixed: Fix jar files that cannot be scanned in `ClassPathScanner` issue
- Fixed: An issue when `AbstractAgileLoggerFilter.URL_MAPPING.get(uri)` return null, throws NullPointerException
- Perf: Modify the async mode default thread maximum-pool-size is cup core size
- Perf: `VersionerAdapter` supports Map, List, Set parameters
- Feature: Provide `AbstractMockerAdapter` support get currently method and args
- Feature: Provide `MethodsMockerAdapter` support for calling by method name
- Feature: Provide `RequestVersionerAdapter` to enable you to override only the request method
- Feature: Provide `ResponseVersionerAdapter` to enable you to override only the response method
### v1.0.6
- Refactor: Properties config file tuning structure
- Fixed: `SpringSyntheticAgileLogger` cache causes is always triggering level error log problem
- Perf: Enhance `JsonUtils` API
- Feature: `RequestLog` add `curl` prop
### v1.0.7
- Fixed: Fix the Chinese encoding problem in curl
- Perf: Optimization When the log level is error, the system can identify the exception
- Perf: Optimization of RPC-Feign
- Perf: Structure change AgileLoggerContext.parentId to AgileLoggerContext.parent
- Feature: RPC-RestTemplate is supported
### v1.0.8
- Fixed: Fix An error occurred when @EnableAgileLogger was not enabled on RPC-Client
- Fixed: Fix null pointer to `parent.getId()` in RPC-Client
- Perf: `AgileLoggerSpringProperties` add uri-prefix property
- Perf: `JsonUtils` remove NamingStrategy for snack case
### v1.0.9
- Fixed: Fix `UrlUtils.queryStringToMap` in split bug
- Perf: `VersionUtils` add version digits compare
- Perf: Optimize the value logic of version key and trackId key. By default, the key is the first in the key-list
- Build: Upgrade dependencies
### v1.0.10
- Fixed: Fixed feignLogger bean naming error in `AgileLoggerFeignAutoConfiguration` class
- Perf: Remove redundant code
- Perf: Optimizing bean names
- Feature: Add `NoneRecordProcessor` so that you don't record log, but need @Versioner or @Mocker feature
- Feature: Add `AgileLoggers` to log manually
### v1.0.11
- Perf: Set the core number of threads to cpu * 2 and maximum number of threads to cpu * 2 + 1 for async default log mode
- Build: Upgrade dependencies
### v1.0.12
- Fixed: Fixed an issue where `@RequestMapping` could not get value attribute when specifying the path attribute
- Fixed: Fixed an issue where modifying the log result attribute would affect the actual return value
- Perf: Use Gson to serialization single object
- Feature: Add `mybatis plugin` to log SQL statements
- Feature: Support for rewriting the value of log result-class field using the `@RewriteField` annotation
### v1.0.13
- Fixed: Fixed `@RewriteField` not being able to overwrite array, collection and map type data
- Perf: Add `@RewriteField` switch
- Perf: Optimized problem with mybatis-plugin not getting the default value of SqlCommandType
### v1.0.14
- Fixed: Fixed NullPointerException being thrown when result is null in AgileLoggerAnnotatedInterceptor
- Perf: Modify the name of the rewrite attr to rewrite-field in properties
### v1.0.15
- Fixed: Fixed the problem with the original message was overridden by default in `R.class` when setCode is called
- Fixed: Fixed `@RewriteField` throw NullPointerException in iterator Map Object
### v1.0.16
- Fixed: Fixed the Rewrite Bug
- Perf: Readjust the way you rewrite and configure, You must specify a rewrite packages
### v2.0.0
- Refactor: Support Java 17
### v2.0.1
- Fixed: Fixed a version issue that caused the download to fail
### v2.1.0
- Refactor: R.class structure refactor
### v2.2.0
- Fixed: Java 8 date/time type `java.time.LocalDateTime` not supported by default
- Perf: `StringUtils` add `print` method
- Build: Upgrade Spring-boot version to v3.1.0
### v2.2.1
- Feature: Support `@PathVariable` type
### v2.2.2
- Feature: Feign plugin Support ignore method
- Fixed: Fixed an issue where `TransactionUtils` did not clean up "track-id" in the thread
### v2.2.3
- Feature: Support specified package path to scanning
- Fixed: Fixed an issue where class request mapping url does not start with "/"
- Fixed: Fixed an issue where Duplicate key in response headers
### v2.2.4
- Perf: Support `server.servlet.context-path`, And the priority is lower than that of the property `uri-prefix`
- Perf: Optimized how `AgileLoggerContext` gets `basePackages`
### v2.2.5
- Fixed (agile-logger-plugin-mybatis): Fixed an issue for parameterTypes when parameters have empty item
- Fixed (agile-logger-spring): Fixed an issue for `PutMappingProcessor`