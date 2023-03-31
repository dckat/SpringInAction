package tacos;

import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class OrderSubmitMessageHandler implements GenericHandler<Order> {
    private RestTemplate restTemplate;
    private ApiProperties apiProperties;

    public OrderSubmitMessageHandler(
            ApiProperties apiProperties, RestTemplate restTemplate) {
        this.apiProperties = apiProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public Object handle(Order order, MessageHeaders headers) {
        restTemplate.postForObject(apiProperties.getUrl(), order, String.class);
        return null;
    }
}