package com.app.Util;

import com.app.dto.EmailMessage;
import com.app.service.EmailMessageListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.group.id}")
    private String groupId;

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
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Kafka security configurations
        configs.put("security.protocol", "SASL_SSL");
        configs.put("sasl.mechanism", "SCRAM-SHA-256");
        configs.put("sasl.jaas.config", saslJaasConfig);

        configs.put("ssl.truststore.location", truststoreLocation);
        configs.put("ssl.truststore.password", truststorePassword);
        configs.put("ssl.keystore.location", keystoreLocation);
        configs.put("ssl.keystore.password", keystorePassword);
        configs.put("ssl.key.password", keyPassword);

        // Additional configurations
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // Reset offset to earliest
        configs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);  // Max records per poll
        configs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);  // Max interval between polls

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);  // Adjust concurrency based on load
        return factory;
    }

//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, EmailMessage>> kafkaListenerContainerFactory(
//            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
//            ConsumerFactory<String, EmailMessage> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, EmailMessage> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        configurer.configure(factory, consumerFactory);
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
//        return factory;
//    }


    @Bean
    public MessageConverter messageConverter() {
        return new MappingJackson2MessageConverter();
    }
}
