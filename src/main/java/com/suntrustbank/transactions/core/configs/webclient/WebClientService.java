package com.suntrustbank.transactions.core.configs.webclient;

public interface WebClientService<I, O> {

    ProviderConfigure configure();

    O request(I requestDto);
}
