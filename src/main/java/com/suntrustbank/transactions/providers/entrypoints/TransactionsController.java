package com.suntrustbank.transactions.providers.entrypoints;


import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.transactions.core.utils.jwt.JwtUtil;
import com.suntrustbank.transactions.providers.dtos.*;
import com.suntrustbank.transactions.providers.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.suntrustbank.transactions.core.utils.jwt.JwtUtil.USER_NAME;
import static com.suntrustbank.transactions.core.utils.jwt.JwtUtil.USER_AGENT;

@RestController
@RequestMapping("/v1/transaction")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;
    private final JwtUtil jwtService;

    @PostMapping("/pin")
    public ResponseEntity setTransactionPin(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated PinRequest pinRequest) throws GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        pinRequest.setInitiatorId(userId);
        return ResponseEntity.ok(transactionService.setPin(pinRequest));
    }

    @PostMapping("/credit/virtual-account/transfer")
    public ResponseEntity creditAccountViaTransfer(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated CreditWebhook webhook) throws GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        webhook.setInitiatorId(userId);
        return ResponseEntity.ok(transactionService.creditVirtualAccountViaTransfer(webhook));
    }

    @PostMapping("/credit/virtual-account/pos")
    public ResponseEntity creditAccountViaPos(@RequestHeader("Authorization") String authorizationHeader, @RequestBody @Validated POSRequest posRequest) throws GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        posRequest.setInitiatorId(userId);
        return ResponseEntity.ok(transactionService.creditVirtualAccountViaPOS(posRequest));
    }

    @PostMapping("/debit/transfer")
    public ResponseEntity debitAccountViaTransfer(@RequestHeader("Authorization") String authorizationHeader, @RequestHeader(USER_AGENT) String userAgent ,@RequestBody @Validated EncryptedRequest encryptedRequest) throws GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        encryptedRequest.setInitiatorId(userId);
        encryptedRequest.setUserAgent(userAgent);
        return ResponseEntity.ok(transactionService.transfer(encryptedRequest));
    }


    @GetMapping
    public ResponseEntity getTransactions(@RequestHeader("Authorization") String authorizationHeader) throws GenericErrorCodeException {
        var userId = (String) jwtService.extractAllClaims(authorizationHeader, USER_NAME).orElseThrow(GenericErrorCodeException::unAuthorizedToken);
        return ResponseEntity.ok(transactionService.getAll(userId));
    }
}

