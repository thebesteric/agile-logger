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
- Feature: `MetricsRequestLoggerProcessor` support `maxResponseTrackId` and `minResponseTrackId`
- Perf: Optimize @Mocker execution timing
- Perf: RequestLog default tag name is "default", You can use RequestLogProcessor to change it
- Style: `VersionAdapter` rename to `VersionerAdapter`, `AbstractVersionAdapter` rename to `AbstractVersionerAdapter`
