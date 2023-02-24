package tacos.send;

import tacos.Order;

public interface OrderMessagingService {

    public void sendOrder(Order order);
}
