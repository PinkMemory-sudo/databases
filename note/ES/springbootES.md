添加依赖

```xml
<dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
            <version>5.6.8</version>
</dependency>
```

properties中显式指定版本

```xml
<properties>
  <java.version>1.8</java.version>
  <elasticsearch.version>5.6.8</elasticsearch.version>
</properties>
```

添加ES配置类

```java
@Configuration
public class ESConfig {

    @Bean
    public TransportClient client() throws UnknownHostException {
        // Note that you have to set the cluster name if you use one different than "elasticsearch"
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        return client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.123.30.34"), 8300));
    }
}
```







# ?

elasticsearch-rest-high-level-client