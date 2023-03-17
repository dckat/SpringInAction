package tacos.messaging.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import tacos.KitchenUI;
import tacos.Order;

@Profile("kafka-listener")
@Component
@Slf4j
public class KafkaOrderListener {

    private KitchenUI ui;

    public KafkaOrderListener(KitchenUI ui) {
        this.ui = ui;
    }

    @KafkaListener(topics = "tacocloud.orders.topic")
    public void handle(Order order, ConsumerRecord<String, Order> record) {
        log.info("Received from partition {} with timestamp {}",
                record.partition(), record.timestamp());

        ui.displayOrder(order);
    }
}
