package com.suntrustbank.transactions.core.configs.webclient;

import io.netty.channel.ChannelHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@Builder
public class ProviderConfigure {

    private String baseUrl;
    private HashMap<String, String> headers;
    private ArrayList<ChannelHandler> channelHandlers;
    private ArrayList<ExchangeFilterFunction> filterFunctions;

    public void validate() {
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("ProviderConfigure.baseUrl invalid");
        }
    }
}
