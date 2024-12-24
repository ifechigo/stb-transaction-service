package com.suntrustbank.transactions.providers.services.impl;

import com.suntrustbank.transactions.core.dtos.BaseResponse;
import com.suntrustbank.transactions.core.enums.BaseResponseMessage;
import com.suntrustbank.transactions.core.enums.ErrorCode;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.transactions.core.utils.AESUtil;
import com.suntrustbank.transactions.core.utils.FieldValidatorUtil;
import com.suntrustbank.transactions.core.utils.UUIDGenerator;
import com.suntrustbank.transactions.providers.dtos.*;
import com.suntrustbank.transactions.providers.dtos.enums.Source;
import com.suntrustbank.transactions.providers.repository.*;
import com.suntrustbank.transactions.providers.repository.enums.TransactionStatus;
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
import java.time.Instant;
import java.util.Optional;

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

    private static final String INVALID_REQUEST = "invalid request";


    @Override
    public BaseResponse setPin(PinRequest request) throws GenericErrorCodeException {
        try {
            String pin = aesUtil.decrypt(request.getPin(), String.class);
            if (!pin.matches("\\d{4}")) {
                throw GenericErrorCodeException.badRequest(INVALID_REQUEST);
            }

            Optional<TransactionPin> existingTransactionPin = transactionPinRepository.findByUserId(request.getInitiatorId());
            if (existingTransactionPin.isPresent()) {
                return BaseResponse.success("transaction pin already exist", BaseResponseMessage.SUCCESSFUL);
            }

            TransactionPin transactionPin = new TransactionPin();
            transactionPin.setId(UUIDGenerator.generate());
            transactionPin.setUserId(request.getInitiatorId());
            transactionPin.setPin(request.getPin());
            transactionPinRepository.save(transactionPin);
        } catch (GenericErrorCodeException e) {
            throw GenericErrorCodeException.badRequest(INVALID_REQUEST);
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

    @Transactional
    public BaseResponse creditVirtualAccountViaPOS(POSRequest request) throws GenericErrorCodeException {
        Optional<Transaction> existingTransaction = transactionRepository.findByReference(request.getReference());
        if (existingTransaction.isPresent()) {
            throw GenericErrorCodeException.badRequest("duplicate transaction");
        }

        Transaction transaction = new Transaction();
        transaction.setId(UUIDGenerator.generate());
        transaction.setInitiatorId(request.getInitiatorId());
        transaction.setReference(request.getReference());
        transaction.setTransactionType(TransactionType.POS);
        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setFee(request.getServiceFee());
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setStatusDescription("Processing");
        transactionRepository.save(transaction);
        POSTransaction posTransaction = new POSTransaction();
        posTransaction.setId(UUIDGenerator.generate());
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

        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setStatusDescription("Approved");
        transaction.setUpdatedAt(Instant.now().toEpochMilli());
        transactionRepository.save(transaction);

        return BaseResponse.success(posTransaction, BaseResponseMessage.SUCCESSFUL);
    }

    @Transactional
    public BaseResponse transfer(EncryptedRequest request) throws GenericErrorCodeException {
        TransferRequest transferRequest;
        try {
            transferRequest = aesUtil.decrypt(request.getData(), TransferRequest.class);
            FieldValidatorUtil.validate(transferRequest);
            Source.valueOf(transferRequest.getSource());
        } catch (Exception e) {
            log.info("==> invalid transfer request. Error: [{}]", e.getMessage(), e);
            throw GenericErrorCodeException.badRequest(INVALID_REQUEST);
        }

        Optional<TransactionPin> transactionPin = transactionPinRepository.findByUserId(request.getInitiatorId());
        if (transactionPin.isEmpty()) {
            log.info("==> invalid user");
            throw GenericErrorCodeException.badRequest(INVALID_REQUEST);
        }

        if (!transferRequest.getPin().equals(transactionPin.get().getPin())) {
            log.info("==> invalid pin");
            throw GenericErrorCodeException.badRequest(INVALID_REQUEST);
        }

        Transaction transaction = new Transaction();
        transaction.setId(UUIDGenerator.generate());
        transaction.setInitiatorId(request.getInitiatorId());
        transaction.setReference(UUIDGenerator.generate());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setAmount(transferRequest.getAmount());
        transaction.setFee(0);
        transaction.setDescription(transferRequest.getDescription());
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setStatusDescription("Processing");
        transactionRepository.save(transaction);
        DebitTransaction debitTransaction = new DebitTransaction();
        debitTransaction.setId(UUIDGenerator.generate());
        debitTransaction.setTransaction(transaction);
        debitTransaction.setSource(Source.valueOf(transferRequest.getSource()));
        debitTransaction.setUserAgent(request.getUserAgent());
        debitTransactionRepository.save(debitTransaction);

        EncryptedRequest newEncryptedRequest = new EncryptedRequest();
        newEncryptedRequest.setData(aesUtil.encrypt(transferRequest));
        messagingService.notifyPayment(newEncryptedRequest);

        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setStatusDescription("Approved");
        transaction.setUpdatedAt(Instant.now().toEpochMilli());
        transactionRepository.save(transaction);

        return BaseResponse.success(debitTransaction, BaseResponseMessage.SUCCESSFUL);
    }

    @Override
    public BaseResponse getAll(String initiatorId) throws GenericErrorCodeException {
        Pageable pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());
        return BaseResponse.success(transactionRepository.findAllByInitiatorId(initiatorId, pageable), BaseResponseMessage.SUCCESSFUL);
    }
}
