package com.app.Util;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;


@Configuration

public class KafkaProducerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.sasl.jaas.config}")
    private String saslJaasConfig;

    @Value("${kafka.ssl.truststore.location}")
    private String truststoreLocation;

    @Value("${kafka.ssl.truststore.password}")
    private String truststorePassword;

    @Value("${kafka.ssl.keystore.location}")
    private String keystoreLocation;

    @Value("${kafka.ssl.keystore.password}")
    private String keystorePassword;

    @Value("${kafka.ssl.key.password}")
    private String keyPassword;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        configs.put("security.protocol", "SASL_SSL");
        configs.put("sasl.mechanism", "SCRAM-SHA-256");
        configs.put("sasl.jaas.config", saslJaasConfig);

        configs.put("ssl.truststore.location", truststoreLocation);
        configs.put("ssl.truststore.password", truststorePassword);
        configs.put("ssl.keystore.location", keystoreLocation);
        configs.put("ssl.keystore.password", keystorePassword);
        configs.put("ssl.key.password", keyPassword);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
