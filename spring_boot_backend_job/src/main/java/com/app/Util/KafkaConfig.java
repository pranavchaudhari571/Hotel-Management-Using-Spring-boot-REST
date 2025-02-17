//package com.app.Util;
//
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainerFactory;
//import org.springframework.kafka.listener.MessageListenerContainer;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.support.converter.Jackson2JsonMessageConverter;
//
//@EnableKafka
//public class KafkaConfig {
//
//    public ConcurrentMessageListenerContainerFactory<Integer, String> messageListenerContainerFactory() {
//        ConcurrentMessageListenerContainerFactory<Integer, String> factory =
//                new ConcurrentMessageListenerContainerFactory<>();
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
//        return factory;
//    }
//}
