package org.example.service;

import org.example.dto.ProcessorPerformTransactionResult;
import org.example.events.PerformTransactionEvent;
import org.example.events.PerformTransactionResultEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumerService {

    @Value("${payments.results.topic.name}")
    private String paymentsResultsTopicName;
    private final RestTemplate restTemplate;

    private final KafkaTemplate<String, PerformTransactionResultEvent> paymentsResultsKafkaTemplate;

    public ConsumerService(RestTemplate restTemplate, KafkaTemplate<String, PerformTransactionResultEvent> paymentsResultsKafkaTemplate) {
        this.restTemplate = restTemplate;
        this.paymentsResultsKafkaTemplate = paymentsResultsKafkaTemplate;
    }

    @KafkaListener(
            topics = "${payments.process.topic.name}",
            groupId = "${payments.process.topic.group.id}",
            containerFactory = "processTransactionKafkaListenerContainerFactory"
    )
    private void sendPerformTransactionToProcessorService(PerformTransactionEvent transactionEvent) {
        final ProcessorPerformTransactionResult processorPerformTransactionResult = restTemplate.postForObject(
                "http://localhost:8080/processor/perform_transaction", transactionEvent, ProcessorPerformTransactionResult.class);

        final long transactionId = processorPerformTransactionResult.getTransactionId();
        //consumer saves result (along with transactionId in DB)
        final PerformTransactionResultEvent performTransactionResultEvent = convertProcessorResultToTransactionResultEvent(processorPerformTransactionResult);
        sendToPaymentsResultTopic(performTransactionResultEvent);
    }

    private void sendToPaymentsResultTopic(PerformTransactionResultEvent performTransactionResultEvent) {
        Message<PerformTransactionResultEvent> message = MessageBuilder
                .withPayload(performTransactionResultEvent)
                .setHeader(KafkaHeaders.TOPIC, paymentsResultsTopicName)
                .build();
        paymentsResultsKafkaTemplate.send(message);
    }

    private PerformTransactionResultEvent convertProcessorResultToTransactionResultEvent(ProcessorPerformTransactionResult processorPerformTransactionResult) {
        PerformTransactionResultEvent performTransactionResultEvent = new PerformTransactionResultEvent();
        performTransactionResultEvent.setTransactionId(processorPerformTransactionResult.getTransactionId());
        performTransactionResultEvent.setDstBankAccount(processorPerformTransactionResult.getDstBankAccount());
        return performTransactionResultEvent;
    }
}
