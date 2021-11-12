package com.konstantinov.onlinestore.bd.orders;

import com.konstantinov.onlinestore.rest.dto.Order;

public interface OrderService {
    public void createOrder(Order order);
    public Order getOrderById(Long id);
}
