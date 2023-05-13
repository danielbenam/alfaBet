package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PerformAdvanceRequestDto {

    @NotNull(message = "dst_bank_account must not be null")
    private long dst_bank_account;

    @NotNull(message = "amount must not be null")
    @Min(value=0, message="amount must be equal or greater than 0")
    private long amount;
}

