package tacos.messaging.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tacos.Order;

@Component
public class RabbitOrderReceiver {
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitOrderReceiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Order receiveOrder() {
        return (Order) rabbitTemplate.receiveAndConvert("tacocloud.order.queue");
    }
}
