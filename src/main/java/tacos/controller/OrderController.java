package tacos.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tacos.domain.Order;
import tacos.repository.OrderRepository;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

    private OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/current")
    public String orderForm() {
        return "orderForm";
    }

    @GetMapping("/{deliveryZip}")
    public String getOrder(@PathVariable String deliveryZip) {
        List<Order> orderList = orderRepository.findByDeliveryZip(deliveryZip);

        for (Order order : orderList) {
            System.out.println(order);
        }
        return "test";
    }

    @PostMapping
    public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "orderForm";
        }

        orderRepository.save(order);
        sessionStatus.setComplete();
        return "redirect:/";
    }
}
