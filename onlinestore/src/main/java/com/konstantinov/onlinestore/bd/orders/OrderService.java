package com.konstantinov.onlinestore.bd.orders;

import com.konstantinov.onlinestore.rest.dto.Order;

import java.util.List;

public interface OrderService {
    public void createOrder(Order order);
    public Order getOrderById(Long id);
    public List<Order> getSomeOrder(Integer page);
    void  updateOrderWithoutCakes(Order order);
    void updateOrder(Order order);
    List<Order> getOrderByNumber(String number, Integer page);
}
