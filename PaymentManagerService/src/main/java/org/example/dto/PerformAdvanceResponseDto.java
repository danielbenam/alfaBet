package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.PaymentStatus;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PerformAdvanceResponseDto {

    private long id;
    private long dst_bank_account;
    private long amount;
    private PaymentStatus paymentStatus;
    private LocalDate creationDate;
}