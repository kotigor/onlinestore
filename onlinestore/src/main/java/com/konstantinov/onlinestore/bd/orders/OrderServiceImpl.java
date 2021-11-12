package com.konstantinov.onlinestore.bd.orders;

import com.konstantinov.onlinestore.bd.goods.CakeEntity;
import com.konstantinov.onlinestore.bd.goods.CakeRepository;
import com.konstantinov.onlinestore.bd.users.UserRepository;
import com.konstantinov.onlinestore.rest.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderCakeRepository orderCakeRepository;
    private final CakeRepository cakeRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderCakeRepository orderCakeRepository,
                            CakeRepository cakeRepository,
                            UserRepository userRepository){
        this.orderRepository = orderRepository;
        this.orderCakeRepository = orderCakeRepository;
        this.cakeRepository = cakeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createOrder(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setDate(LocalDate.parse(order.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        orderEntity.setDelivery(order.getDeliveryMethod());
        orderEntity.setPayment(order.getPayment());
        orderEntity.setStatus(OrderStatus.NEW);
        orderEntity.setUser(userRepository.findByNumber(order.getUser().getNumber()));
        orderRepository.save(orderEntity);
        List<OrderCakeEntity> orderCakeEntities = new ArrayList<>();
        order.getCakes().forEach((k, v) -> {
            CakeEntity cakeEntity = cakeRepository.getById(k);
            if(cakeEntity == null)
                System.out.println("null");
            OrderCakeEntity orderCakeEntity = new OrderCakeEntity();
            orderCakeEntity.setCake(cakeEntity);
            orderCakeEntity.setOrder(orderEntity);
            orderCakeEntity.setCount(v);
            orderCakeRepository.save(orderCakeEntity);
        });
    }

    @Override
    public Order getOrderById(Long id) {
        return null;
    }
}
