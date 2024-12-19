package com.suntrustbank.transactions.providers.services.impl;

import com.suntrustbank.transactions.core.dtos.BaseResponse;
import com.suntrustbank.transactions.core.enums.BaseResponseMessage;
import com.suntrustbank.transactions.core.enums.ErrorCode;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.transactions.core.utils.AESUtil;
import com.suntrustbank.transactions.providers.dtos.CreditWebhook;
import com.suntrustbank.transactions.providers.dtos.POSRequest;
import com.suntrustbank.transactions.providers.dtos.PinRequest;
import com.suntrustbank.transactions.providers.dtos.TransferRequest;
import com.suntrustbank.transactions.providers.repository.*;
import com.suntrustbank.transactions.providers.repository.enums.Status;
import com.suntrustbank.transactions.providers.repository.enums.TransactionType;
import com.suntrustbank.transactions.providers.repository.models.*;
import com.suntrustbank.transactions.providers.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final DebitTransactionRepository debitTransactionRepository;
    private final POSTransactionRepository posTransactionRepository;
    private final TransactionPinRepository transactionPinRepository;
    private final AESUtil aesUtil;


    @Override
    public BaseResponse setPin(PinRequest request) throws GenericErrorCodeException {
        try {
            TransactionPin transactionPin = new TransactionPin();
            transactionPin.setId(UUID.randomUUID().toString());
            transactionPin.setBusinessId(request.getBusinessId());
            transactionPin.setPin(aesUtil.encrypt(request.getNewPin()));
            transactionPinRepository.save(transactionPin);
        } catch (Exception e) {
            throw new GenericErrorCodeException("failed to set transaction pin, please try again", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        return BaseResponse.success("SUCCESS", BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse creditVirtualAccountViaTransfer(CreditWebhook webhook) throws GenericErrorCodeException {

        Transaction transaction = new Transaction();
        transaction.setInitiatorId(webhook.getInitiatorId());
        transaction.setReference(webhook.getReference());
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionDate(new Date());
        transaction.setAmount(BigDecimal.valueOf(Double.parseDouble(webhook.getAmount())));
        transaction.setFee(Double.parseDouble(webhook.getFees()));
        transaction.setDescription(webhook.getDescription());
        transactionRepository.save(transaction);
        CreditTransaction creditTransaction = new CreditTransaction();
        creditTransaction.setTransaction(transaction);
        creditTransactionRepository.save(creditTransaction);

        return BaseResponse.success("Processing", BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse creditVirtualAccountViaPOS(POSRequest request) throws GenericErrorCodeException {


        Transaction transaction = new Transaction();
        transaction.setInitiatorId(request.getAgentAccountNumber());
        transaction.setReference(request.getReference());
        transaction.setTransactionType(TransactionType.POS);
        transaction.setTransactionDate(new Date());
        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setFee(request.getServiceFee());
        transaction.setStatusDescription(request.getStatusDescription());
        transactionRepository.save(transaction);
        POSTransaction posTransaction = new POSTransaction();
        posTransaction.setTransaction(transaction);
        posTransactionRepository.save(posTransaction);


        return BaseResponse.success("", BaseResponseMessage.SUCCESSFUL);
    }

    public BaseResponse transfer(TransferRequest request) throws GenericErrorCodeException {

        Optional<TransactionPin> transactionPin = transactionPinRepository.findByBusinessId(request.getInitiatorId());
        if (transactionPin.isEmpty()) {
            throw new GenericErrorCodeException("invalid business", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        if (!aesUtil.validatePin(request.getPin(), transactionPin.get().getPin())) {
            throw new GenericErrorCodeException("invalid pin", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setInitiatorId(request.getInitiatorId());
        transaction.setReference(request.getReference());
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionDate(new Date());
        transaction.setAmount(BigDecimal.valueOf(Double.parseDouble(request.getAmount())));
        transaction.setFee(Double.parseDouble("0"));
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Status.SUCCESSFUL);
        transaction.setStatusDescription("Approved");
        transactionRepository.save(transaction);
        DebitTransaction debitTransaction = new DebitTransaction();
        debitTransaction.setId(UUID.randomUUID().toString());
        debitTransaction.setTransaction(transaction);
        debitTransactionRepository.save(debitTransaction);
        return BaseResponse.success(debitTransaction, BaseResponseMessage.SUCCESSFUL);
    }
}
