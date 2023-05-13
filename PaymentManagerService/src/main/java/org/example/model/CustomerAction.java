package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.CustomerActionType;
import org.example.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dstBankAccount;

    private Long amount;

    private LocalDate creationDate;

    private CustomerActionType customerActionType;

    private PaymentStatus paymentStatus;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "action_id")
    private List<PaymentTransaction> paymentTransactions = new ArrayList<>();


}
