package com.konstantinov.onlinestore.rest.controller;

import com.konstantinov.onlinestore.bd.orders.OrderService;
import com.konstantinov.onlinestore.bd.users.UserService;
import com.konstantinov.onlinestore.rest.dto.Order;
import com.konstantinov.onlinestore.rest.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService){
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping(value = "order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createOrder(@Valid @RequestBody Order order){
        User user = userService.getOrCreateUser(order.getUser());
        order.setUser(user);
        orderService.createOrder(order);
    }
}
