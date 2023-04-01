package net.provera.productserv.service.impl;

import net.provera.productserv.config.RabbitMQConfig;
import net.provera.productserv.event.ProductEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventSender {
    private final RabbitTemplate rabbitTemplate;

    public ProductEventSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendProductEvent(ProductEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, event);
    }
}
