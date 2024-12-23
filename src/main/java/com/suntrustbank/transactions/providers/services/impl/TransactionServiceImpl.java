package com.suntrustbank.transactions.providers.services.impl;

import com.suntrustbank.transactions.core.dtos.BaseResponse;
import com.suntrustbank.transactions.core.enums.BaseResponseMessage;
import com.suntrustbank.transactions.core.enums.ErrorCode;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.transactions.core.utils.AESUtil;
import com.suntrustbank.transactions.core.utils.TypeValidationUtil;
import com.suntrustbank.transactions.providers.dtos.*;
import com.suntrustbank.transactions.providers.repository.*;
import com.suntrustbank.transactions.providers.repository.enums.Status;
import com.suntrustbank.transactions.providers.repository.enums.TransactionType;
import com.suntrustbank.transactions.providers.repository.models.*;
import com.suntrustbank.transactions.providers.services.MessagingService;
import com.suntrustbank.transactions.providers.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final DebitTransactionRepository debitTransactionRepository;
    private final POSTransactionRepository posTransactionRepository;
    private final TransactionPinRepository transactionPinRepository;
    private final MessagingService messagingService;
    private final AESUtil aesUtil;


    @Override
    public BaseResponse setPin(PinRequest request) throws GenericErrorCodeException {
        try {
            TransactionPin transactionPin = new TransactionPin();
            transactionPin.setId(UUID.randomUUID().toString());
            transactionPin.setUserId(request.getInitiatorId());
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
        Optional<POSTransaction> existingTransaction = transactionRepository.findByReference(request.getReference());
        if (existingTransaction.isPresent()) {
            throw GenericErrorCodeException.badRequest("duplicate transaction");
        }

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setInitiatorId(request.getInitiatorId());
        transaction.setReference(request.getReference());
        transaction.setTransactionType(TransactionType.POS);
        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setFee(request.getServiceFee());
        transaction.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        transaction.setStatusDescription(request.getStatusDescription());
        transactionRepository.save(transaction);
        POSTransaction posTransaction = new POSTransaction();
        posTransaction.setId(UUID.randomUUID().toString());
        posTransaction.setTransaction(transaction);
        posTransaction.setTransactionDate(request.getTransactionDate());
        posTransaction.setAccountAgentNumber(request.getAgentAccountNumber());
        posTransaction.setRrn(request.getRrn());
        posTransaction.setPan(request.getPan());
        posTransaction.setStan(request.getStan());
        posTransaction.setCardExpiry(request.getCardExpiry());
        posTransaction.setStatusCode(request.getStatusCode());
        posTransaction.setCustomerName(request.getCustomerName());
        posTransaction.setTerminalId(request.getTerminalId());
        posTransaction.setSerialNumber(request.getSerialNumber());
        posTransaction.setInvoiceId(request.getInvoiceId());
        posTransactionRepository.save(posTransaction);

        if (!request.getStatus().equalsIgnoreCase("success")) {
            throw GenericErrorCodeException.badRequest("request cannot be processed because the transaction failed");
        }

        return BaseResponse.success(transaction, BaseResponseMessage.SUCCESSFUL);
    }

    @Transactional
    public BaseResponse transfer(EncryptedRequest request, String initiatorId) throws GenericErrorCodeException {
        TransferRequest req;

        try {
            req = TypeValidationUtil.validateType(aesUtil.decrypt(request.getData()), TransferRequest.class);
        } catch (Exception e) {
            log.info("Invalid transfer request: Error {}", e.getMessage(), e);
            throw GenericErrorCodeException.badRequest("invalid request");
        }

        Optional<TransactionPin> transactionPin = transactionPinRepository.findByUserId(initiatorId);
        if (transactionPin.isEmpty()) {
            throw new GenericErrorCodeException("invalid business", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        if (!req.getPin().equals(transactionPin.get().getPin())) {
            throw new GenericErrorCodeException("invalid pin", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setInitiatorId(initiatorId);
        transaction.setReference(UUID.randomUUID().toString());
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setAmount(req.getAmount());
        transaction.setFee(0);
        transaction.setDescription(req.getDescription());
        transaction.setStatus(Status.SUCCESS);
        transaction.setStatusDescription("Approved");
        transactionRepository.save(transaction);
        DebitTransaction debitTransaction = new DebitTransaction();
        debitTransaction.setId(UUID.randomUUID().toString());
        debitTransaction.setTransaction(transaction);
        debitTransactionRepository.save(debitTransaction);

        messagingService.notifyPayment(req);

        return BaseResponse.success(debitTransaction, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse getAll(String initiatorId) throws GenericErrorCodeException {
        Pageable pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());
        return BaseResponse.success(transactionRepository.findAllByInitiatorId(initiatorId, pageable), BaseResponseMessage.SUCCESSFUL);
    }
}
