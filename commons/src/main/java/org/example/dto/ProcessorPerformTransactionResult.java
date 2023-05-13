package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProcessorPerformTransactionResult {

    private long transactionId;

    private long srcBankAccount;

    private long dstBankAccount;

    private long amount;

    private String direction;
}
