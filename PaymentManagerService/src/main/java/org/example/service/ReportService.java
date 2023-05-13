package org.example.service;

import jakarta.transaction.Transactional;
import org.example.enums.TransactionStatus;
import org.example.events.SearchForTransactionReportEvent;
import org.example.events.TransactionStatusEvent;
import org.example.model.PaymentTransaction;
import org.example.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    @Value("${search.in.billing.topic.name}")
    private String searchInBillingTopicName;
    private final PaymentTransactionRepository transactionRepository;
    private final KafkaTemplate<String, SearchForTransactionReportEvent> searchInBillingKafkaTemplate;

    public ReportService(PaymentTransactionRepository transactionRepository, KafkaTemplate<String, SearchForTransactionReportEvent> searchInBillingKafkaTemplate) {
        this.transactionRepository = transactionRepository;
        this.searchInBillingKafkaTemplate = searchInBillingKafkaTemplate;
    }

    @Scheduled(cron = "${interval-in-search-transactions-status-cron}")
    @Transactional
    public void searchForTransactionStatus() {
        final List<PaymentTransaction> paymentTransactions = transactionRepository.findByStatus(TransactionStatus.WAITING_TO_REPORT);
        LocalDate fiveDaysBeforeCurrentDay = LocalDate.now().minusDays(5);

        for (PaymentTransaction transaction : paymentTransactions) {
            if (transaction.getPaymentDate().isBefore(fiveDaysBeforeCurrentDay)) {
                transaction.setStatus(TransactionStatus.NEED_MORE_INVESTIGATION);
            } else {
                sendToSearchInBillingTopic(transaction);
            }
        }
    }

    private void sendToSearchInBillingTopic(PaymentTransaction transaction) {
        SearchForTransactionReportEvent searchForTransactionReportEvent = new SearchForTransactionReportEvent();
        searchForTransactionReportEvent.setTransactionId(transaction.getTransactionId());
        Message<SearchForTransactionReportEvent> message = MessageBuilder
                .withPayload(searchForTransactionReportEvent)
                .setHeader(KafkaHeaders.TOPIC, searchInBillingTopicName)
                .build();
        searchInBillingKafkaTemplate.send(message);
    }


    @KafkaListener(
            topics = "${transaction.status.results.topic.name}",
            groupId = "${transaction.status.results.topic.group.id=}",
            containerFactory = "transactionStatusKafkaListenerContainerFactory"
    )
    @Transactional
    public void readingTransactionStatus(TransactionStatusEvent transactionStatusEvent){
        final PaymentTransaction paymentTransaction = transactionRepository.findByTransactionId(transactionStatusEvent.getTransactionId());
        paymentTransaction.setStatus(TransactionStatus.createFrom(transactionStatusEvent.getStatus()));
        // no need to save managed entity
    }

}
