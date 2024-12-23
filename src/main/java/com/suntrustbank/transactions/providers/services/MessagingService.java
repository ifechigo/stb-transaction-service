package com.suntrustbank.transactions.providers.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.suntrustbank.transactions.providers.dtos.enums.PublisherDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {

    private final RabbitTemplate rabbitTemplate;

    public void notifyPayment(Object request) {
        try {
            rabbitTemplate.convertAndSend(PublisherDetails.PAYMENT_ROUTING_KEY.getValue(), new ObjectMapper().writeValueAsString(request)); // Only routing key required
            log.info("==> Payment Notification Successful ===");
        } catch (Exception e) {
            log.info("==> Failed to publish message. Error: {}",e.getMessage() , e);
        }
    }
}
