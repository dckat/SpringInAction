package tacos.messaging.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tacos.Order;
import tacos.OrderReceiver;

@Component
public class JmsOrderReceiver implements OrderReceiver {
    private JmsTemplate jmsTemplate;

    @Autowired
    public JmsOrderReceiver(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public Order receiveOrder() {
        return (Order) jmsTemplate.receiveAndConvert("tacocloud.order.queue");
    }
}
