# agile-logger
A flexible logging plug-in



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