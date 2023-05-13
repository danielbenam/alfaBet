package org.example.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PerformTransactionEvent {

    private long srcBankAccount;

    private long dstBankAccount;

    private long amount;

    private String direction;
}
