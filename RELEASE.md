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
- Feature: RPC-RestTemplate is supported
- Perf: Optimization of RPC-Feign
- Perf: Structure change AgileLoggerContext.parentId to AgileLoggerContext.parent
### v1.0.8
- Fixed: Fix An error occurred when @EnableAgileLogger was not enabled on RPC-Client
- Fixed: Fix null pointer to `parent.getId()` in RPC-Client
- Perf: `AgileLoggerSpringProperties` add uri-prefix property
- Perf: `JsonUtils` remove NamingStrategy for snack case
### v1.0.9
- Fixed: Fix `UrlUtils.queryStringToMap` in split bug
- Perf: `VersionUtils` add version digits compare
- Perf: Optimize the value logic of version key and trackId key. By default, the value is the first in the list