package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.TransactionStatus;

import java.time.LocalDate;

@Entity
@Table(indexes = {@Index(name = "dst_Bank_index",  columnList="dstBankAccount", unique = true),
        @Index(name = "transaction_Id_index", columnList="transactionId")})
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transactionId;

    private Long dstBankAccount;

    private Long amount;

    private LocalDate paymentDate;

    private TransactionStatus status;

}
