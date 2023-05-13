package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.PerformAdvanceRequestDto;
import org.example.dto.PerformAdvanceResponseDto;
import org.example.model.CustomerAction;
import org.example.service.PaymentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }


    @PostMapping("/perform_advance")
    public ResponseEntity<PerformAdvanceResponseDto> performAdvanced(@Valid @RequestBody PerformAdvanceRequestDto performAdvanceRequestDto){
        final CustomerAction action = paymentsService.performAdvanced(performAdvanceRequestDto);
        final PerformAdvanceResponseDto performAdvanceResponseDto = paymentsService.convertEntityToDto(action);
        return ResponseEntity.ok(performAdvanceResponseDto);
    }

}
