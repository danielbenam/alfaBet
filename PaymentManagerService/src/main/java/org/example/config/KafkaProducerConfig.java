package org.example.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.events.SearchForTransactionReportEvent;
import org.example.events.PerformTransactionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServers;



    public Map<String,Object> producerConfig(){
        Map<String,Object> props= new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,boostrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
   //     props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean
    public ProducerFactory<String, PerformTransactionEvent> paymentsProcessProducerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public ProducerFactory<String, SearchForTransactionReportEvent> searchInBillingProducerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, PerformTransactionEvent> paymentsProcessKafkaTemplate(ProducerFactory<String,PerformTransactionEvent> paymentsProcessProducerFactory){
        return new KafkaTemplate<>(paymentsProcessProducerFactory);
    }


    @Bean
    public KafkaTemplate<String, SearchForTransactionReportEvent> searchInBillingKafkaTemplate(ProducerFactory<String, SearchForTransactionReportEvent> searchInBillingProducerFactory){
        return new KafkaTemplate<>(searchInBillingProducerFactory);
    }


}
