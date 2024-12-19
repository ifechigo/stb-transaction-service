package com.suntrustbank.transactions.providers.entrypoints;


import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.transactions.providers.dtos.CreditWebhook;
import com.suntrustbank.transactions.providers.dtos.POSRequest;
import com.suntrustbank.transactions.providers.dtos.PinRequest;
import com.suntrustbank.transactions.providers.dtos.TransferRequest;
import com.suntrustbank.transactions.providers.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @PostMapping("/pin")
    public ResponseEntity setTransactionPin(@RequestBody @Validated PinRequest pinRequest) throws GenericErrorCodeException {
        return ResponseEntity.ok(transactionService.setPin(pinRequest));
    }

    @PostMapping("/credit/virtual-account/transfer")
    public ResponseEntity creditAccountViaTransfer(@RequestBody @Validated CreditWebhook webhook) throws GenericErrorCodeException {
        return ResponseEntity.ok(transactionService.creditVirtualAccountViaTransfer(webhook));
    }

    @PostMapping("/credit'/virtual-account/pos")
    public ResponseEntity creditAccountViaPos(@RequestBody @Validated POSRequest posRequest) throws GenericErrorCodeException {
        return ResponseEntity.ok(transactionService.creditVirtualAccountViaPOS(posRequest));
    }

    @PostMapping("/debit/transfer")
    public ResponseEntity debitAccountViaTransfer(@RequestBody @Validated TransferRequest transferRequest) throws GenericErrorCodeException {
        return ResponseEntity.ok(transactionService.transfer(transferRequest));
    }
}
