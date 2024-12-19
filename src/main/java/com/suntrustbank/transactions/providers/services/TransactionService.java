package com.suntrustbank.transactions.providers.services;

import com.suntrustbank.transactions.core.dtos.BaseResponse;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.transactions.providers.dtos.CreditWebhook;
import com.suntrustbank.transactions.providers.dtos.POSRequest;
import com.suntrustbank.transactions.providers.dtos.PinRequest;
import com.suntrustbank.transactions.providers.dtos.TransferRequest;

public interface TransactionService {
    BaseResponse setPin(PinRequest request) throws GenericErrorCodeException;
    BaseResponse creditVirtualAccountViaTransfer(CreditWebhook webhook) throws GenericErrorCodeException;
    BaseResponse creditVirtualAccountViaPOS(POSRequest request) throws GenericErrorCodeException;
    BaseResponse transfer(TransferRequest request) throws GenericErrorCodeException;
}
