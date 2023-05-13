package org.example.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.events.PerformTransactionResultEvent;
import org.example.events.TransactionStatusEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServers;

    @Value(value = "${payments.results.topic.group.id}")
    private String paymentsResultsGroupId;

    @Value(value = "${transaction.status.results.topic.group.id}")
    private String transactionStatusResultsGroupId;

    public ConsumerFactory<String, PerformTransactionResultEvent> performTransactionConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, paymentsResultsGroupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(PerformTransactionResultEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PerformTransactionResultEvent>
    performTransactionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PerformTransactionResultEvent> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(performTransactionConsumerFactory());
        return factory;
    }

    public ConsumerFactory<String, TransactionStatusEvent> transactionStatusConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, transactionStatusResultsGroupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(TransactionStatusEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionStatusEvent>
    transactionStatusKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionStatusEvent> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionStatusConsumerFactory());
        return factory;
    }

//    public Map<String, Object> consumerConfig() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//     //   props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.danielbenami.commons.PersonDto");
//        return props;
//    }
//
//    @Bean
//    public ConsumerFactory<String, PersonDto> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfig());
//    }
//
//    @Bean
//    public KafkaListenerContainerFactory<
//            ConcurrentMessageListenerContainer<String, PersonDto>> factory(
//            ConsumerFactory<String, PersonDto> consumerFactory
//    ) {
//        ConcurrentKafkaListenerContainerFactory<String, PersonDto> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        return factory;
//    }
}
