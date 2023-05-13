package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.PerformAdvanceRequestDto;
import org.example.dto.PerformAdvanceResponseDto;
import org.example.enums.CustomerActionType;
import org.example.enums.PaymentStatus;
import org.example.enums.TransactionStatus;
import org.example.events.PerformTransactionResultEvent;
import org.example.events.PerformTransactionEvent;
import org.example.model.CustomerAction;
import org.example.model.PaymentTransaction;
import org.example.repository.CustomerActionRepository;
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
import java.util.stream.IntStream;

@Service
public class PaymentsService {

    @Value("${payments.process.topic.name}")
    private String paymentsProcessTopicName;
    private final CustomerActionRepository actionRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final KafkaTemplate<String, PerformTransactionEvent> paymentsProcessKafkaTemplate;


    public PaymentsService(CustomerActionRepository actionRepository, PaymentTransactionRepository transactionRepository, KafkaTemplate<String, PerformTransactionEvent> paymentsProcessKafkaTemplate) {
        this.actionRepository = actionRepository;
        this.transactionRepository = transactionRepository;
        this.paymentsProcessKafkaTemplate = paymentsProcessKafkaTemplate;
    }

    public CustomerAction performAdvanced(PerformAdvanceRequestDto performAdvanceRequestDto) {
        CustomerAction action = convertDtoToEntity(performAdvanceRequestDto);
        creatingPaymentTransactionsForAction(action);
        final CustomerAction savedAction = actionRepository.save(action);
        return savedAction;
    }

    private void creatingPaymentTransactionsForAction(CustomerAction action) {
        IntStream.range(0, 12).forEach(i -> createPaymentTransaction(i, action));
    }

    private void createPaymentTransaction(int i, CustomerAction action) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setDstBankAccount(action.getDstBankAccount());
        paymentTransaction.setAmount(action.getAmount() / 12);
        paymentTransaction.setPaymentDate(action.getCreationDate().plusWeeks(i));
        paymentTransaction.setStatus(TransactionStatus.WAITING_TO_PERFORM);
        action.getPaymentTransactions().add(paymentTransaction);
    }

    @Scheduled(cron = "${interval-in-perform-transactions-cron}")
    @Transactional
    public void performTransactions() {
        LocalDate currentDay = LocalDate.now();
        final List<PaymentTransaction> paymentTransactions = transactionRepository.
                findByPaymentDateIsBetweenAndStatus(currentDay.minusDays(1), currentDay, TransactionStatus.WAITING_TO_PERFORM);
        paymentTransactions.stream().map(this::convertTransactionEntityToPerformTransactionEvent).
                forEach(this::sendToPaymentsProcessTopic);

        paymentTransactions.stream().forEach(x -> x.setStatus(TransactionStatus.WAITING_FOR_TRANSACTION_RESULT));
        transactionRepository.saveAll(paymentTransactions);
    }

    private void sendToPaymentsProcessTopic(PerformTransactionEvent transactionEvent) {
        Message<PerformTransactionEvent> message = MessageBuilder
                .withPayload(transactionEvent)
                .setHeader(KafkaHeaders.TOPIC, paymentsProcessTopicName)
                .build();
        paymentsProcessKafkaTemplate.send(message);
    }

    @KafkaListener(
            topics = "${payments.results.topic.name}",
            groupId = "${payments.results.topic.group.id}",
            containerFactory = "performTransactionKafkaListenerContainerFactory"
    )
    @Transactional
    public void readingPaymentResults(PerformTransactionResultEvent transactionResultEvent) {
        final PaymentTransaction paymentTransaction = transactionRepository.findByDstBankAccount(transactionResultEvent.getDstBankAccount());
        paymentTransaction.setTransactionId(transactionResultEvent.getTransactionId());
        paymentTransaction.setStatus(TransactionStatus.WAITING_TO_REPORT);
        transactionRepository.save(paymentTransaction);
        // dstBankAccount is index of PaymentTransaction entity - better performance
    }


    private PerformTransactionEvent convertTransactionEntityToPerformTransactionEvent(PaymentTransaction paymentTransaction) {
        PerformTransactionEvent transactionEvent = new PerformTransactionEvent();
        transactionEvent.setSrcBankAccount(100);
        transactionEvent.setDstBankAccount(paymentTransaction.getDstBankAccount());
        transactionEvent.setAmount(paymentTransaction.getAmount());
        transactionEvent.setDirection("debit");
        return transactionEvent;
    }


    private CustomerAction convertDtoToEntity(PerformAdvanceRequestDto performAdvanceRequestDto) {
        CustomerAction action = new CustomerAction();
        action.setDstBankAccount(performAdvanceRequestDto.getDst_bank_account());
        action.setAmount(performAdvanceRequestDto.getAmount());
        action.setCreationDate(LocalDate.now());
        action.setCustomerActionType(CustomerActionType.ADVANCED);
        action.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        return action;
    }

    public PerformAdvanceResponseDto convertEntityToDto(CustomerAction action) {
        PerformAdvanceResponseDto advanceResponseDto = new PerformAdvanceResponseDto();
        advanceResponseDto.setId(action.getId());
        advanceResponseDto.setAmount(action.getAmount());
        advanceResponseDto.setDst_bank_account(action.getDstBankAccount());
        advanceResponseDto.setCreationDate(action.getCreationDate());
        advanceResponseDto.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        return advanceResponseDto;
    }
}
