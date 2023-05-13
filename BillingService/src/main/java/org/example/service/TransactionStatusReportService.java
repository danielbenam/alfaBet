package org.example.service;

import org.example.events.SearchForTransactionReportEvent;
import org.example.events.TransactionStatusEvent;
import org.example.model.ReportResult;
import org.example.repository.ReportResultRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionStatusReportService {

    @Value("${transaction.status.results.topic.name}")
    private String transactionStatusTopicName;

    private final KafkaTemplate<String, TransactionStatusEvent> transactionStatusKafkaTemplate;

    private final ReportResultRepository reportResultRepository;

    public TransactionStatusReportService(KafkaTemplate<String, TransactionStatusEvent> transactionStatusKafkaTemplate, ReportResultRepository reportResultRepository) {
        this.transactionStatusKafkaTemplate = transactionStatusKafkaTemplate;
        this.reportResultRepository = reportResultRepository;
    }

    @KafkaListener(
            topics = "${search.in.billing.topic.name}",
            groupId = "${search.in.billing.topic.group.id}",
            containerFactory = "searchInBillingKafkaListenerContainerFactory"
    )
    public void consumeTransactionsReportEvents(SearchForTransactionReportEvent transactionReportEvent) {
        final Optional<ReportResult> transactionReport = reportResultRepository.findById(transactionReportEvent.getTransactionId());
        transactionReport.ifPresent(this::sendToTransactionStatusTopic);
    }

    private void sendToTransactionStatusTopic(ReportResult reportResult) {
        TransactionStatusEvent transactionReportEvent = convertEntityToTransactionReportEvent(reportResult);
        Message<TransactionStatusEvent> message = MessageBuilder
                .withPayload(transactionReportEvent)
                .setHeader(KafkaHeaders.TOPIC, transactionStatusTopicName)
                .build();
        transactionStatusKafkaTemplate.send(message);
    }

    private TransactionStatusEvent convertEntityToTransactionReportEvent(ReportResult reportResult) {
        TransactionStatusEvent transactionReportEvent = new TransactionStatusEvent();
        transactionReportEvent.setTransactionId(reportResult.getTransactionId());
        transactionReportEvent.setStatus(reportResult.getStatus());
        return transactionReportEvent;
    }

}
