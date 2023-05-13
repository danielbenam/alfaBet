package org.example.repository;

import org.example.enums.TransactionStatus;
import org.example.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction,Long> {

    List<PaymentTransaction> findByPaymentDateIsBetweenAndStatus(LocalDate startPaymentDate, LocalDate endPaymentDate ,TransactionStatus status);

    PaymentTransaction findByDstBankAccount(Long dstBankAccount);

    List<PaymentTransaction> findByStatus(TransactionStatus status);

    PaymentTransaction findByTransactionId(Long transactionId);
}
