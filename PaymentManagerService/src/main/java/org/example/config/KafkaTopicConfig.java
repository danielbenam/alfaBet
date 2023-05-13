package org.example.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${payments.process.topic.name}")
    private String paymentsProcessTopicName;

    @Value("${payments.results.topic.name}")
    private String paymentsResultsTopicName;

    @Value("${search.in.billing.topic.name}")
    private String searchInBilling;

    @Value("${transaction.status.results.topic.name}")
    private String transactionStatusResults;



    @Bean
    public NewTopic paymentsProcessTopic(){
        return TopicBuilder.name(paymentsProcessTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic searchInBillingTopic(){
        return TopicBuilder.name(searchInBilling)
                .partitions(1)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic paymentsResultsTopic(){
        return TopicBuilder.name(paymentsResultsTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transactionStatusResultsTopic(){
        return TopicBuilder.name(transactionStatusResults)
                .partitions(1)
                .replicas(1)
                .build();
    }


}
