package com.pk.esold.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
