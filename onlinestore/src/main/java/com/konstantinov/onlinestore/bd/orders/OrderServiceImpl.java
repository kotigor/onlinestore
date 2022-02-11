package com.konstantinov.onlinestore.bd.orders;

import com.konstantinov.onlinestore.bd.goods.CakeDAO;
import com.konstantinov.onlinestore.bd.goods.CakeEntity;
import com.konstantinov.onlinestore.bd.goods.CakeRepository;
import com.konstantinov.onlinestore.bd.users.UserEntity;
import com.konstantinov.onlinestore.bd.users.UserRepository;
import com.konstantinov.onlinestore.rest.dto.Order;
import com.konstantinov.onlinestore.rest.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    //private final OrderRepository orderRepository;
    //private final CakeRepository cakeRepository;
    private final UserRepository userRepository;
    private final OrderDAO orderRepository;
    private final CakeDAO cakeRepository;

    @Autowired
    public OrderServiceImpl(OrderDAO orderRepository,
                            CakeDAO cakeRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cakeRepository = cakeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createOrder(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        mapCommonInfoOrderToOrderEntity(order, orderEntity);
        orderEntity.setStatus(OrderStatus.NEW);
        orderEntity.setUser(userRepository.findByNumber(order.getUser().getNumber()));
        List<OrderCakeEntity> orderCakeEntities = new ArrayList<>();
        order.getCakes().forEach((k, v) -> {
            CakeEntity cakeEntity = cakeRepository.getById(k);
            OrderCakeEntity orderCakeEntity = new OrderCakeEntity();
            orderCakeEntity.setCake(cakeEntity);
            orderCakeEntity.setOrder(orderEntity);
            orderCakeEntity.setCount(v);
            orderCakeEntities.add(orderCakeEntity);
        });
        orderEntity.setCakes(orderCakeEntities);
        orderRepository.save(orderEntity);
    }

    @Override
    public Order getOrderById(Long id) {
        OrderEntity orderEntity = orderRepository.getById(id);
        return mapOrderEntityToOrder(orderEntity);
    }

    @Override
    public List<Order> getSomeOrder(Integer page) {
        Pageable limit = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "date"));
        //List<OrderEntity> orderEntities = orderRepository.findAll(limit).toList();
        List<OrderEntity> orderEntities = orderRepository.findAll();
        return orderEntities.stream().map(this::mapOrderEntityToOrder).collect(Collectors.toList());
    }

    @Override
    public void updateOrderWithoutCakes(Order order) {
        Long id = order.getId();
        OrderEntity orderEntity = orderRepository.getById(id);
        orderEntity.setDelivery(order.getDeliveryMethod());
        orderEntity.setDate(LocalDate.parse(order.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        orderEntity.setAddress(order.getAddress());
        orderEntity.setPayment(order.getPayment());
        orderEntity.setStatus(order.getStatus());
        orderRepository.save(orderEntity);
    }

    @Override
    public void updateOrder(Order order) {
        OrderEntity orderEntity = orderRepository.getById(order.getId());
        mapCommonInfoOrderToOrderEntity(order, orderEntity);
        orderEntity.setStatus(order.getStatus());

        Set<Long> oldIds = orderEntity.getCakes()
                .stream()
                .map(orderCakeEntity -> orderCakeEntity.getCake().getId())
                .collect(Collectors.toSet());
        Set<Long> newIds = new HashSet<>(order.getCakes().keySet());

        Set<Long> idsToRemove = setDifference(oldIds, newIds);

        Set<Long> idsToAdd = setDifference(newIds, oldIds);

        idsToRemove.forEach(i -> {
            List<OrderCakeEntity> orderCakeToDelete = orderEntity.getCakes()
                    .stream()
                    .filter(j -> j.getCake().getId().equals(i))
                    .collect(Collectors.toList());
            orderEntity.getCakes().removeAll(orderCakeToDelete);
        });

        orderEntity.getCakes().forEach(orderCakeEntity -> orderCakeEntity.setCount(order.getCakes().get(orderCakeEntity.getCake().getId())));

        idsToAdd.forEach(i -> {
            CakeEntity cakeEntity = cakeRepository.getById(i);
            OrderCakeEntity orderCakeEntity = new OrderCakeEntity();
            orderCakeEntity.setCake(cakeEntity);
            orderCakeEntity.setOrder(orderEntity);
            orderCakeEntity.setCount(order.getCakes().get(i));
            orderEntity.getCakes().add(orderCakeEntity);
        });
        orderRepository.save(orderEntity);
    }

    @Override
    public List<Order> getOrderByNumber(String number, Integer page) {
        Pageable limit = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "date"));
        List<OrderEntity> orderEntities = orderRepository.findByUser_Number(number);
        return orderEntities.stream().map(this::mapOrderEntityToOrder).collect(Collectors.toList());
    }

    private void mapCommonInfoOrderToOrderEntity(Order order, OrderEntity orderEntity){
        orderEntity.setDate(LocalDate.parse(order.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        orderEntity.setDelivery(order.getDeliveryMethod());
        orderEntity.setPayment(order.getPayment());
        orderEntity.setAddress(order.getAddress());
    }
    private Order mapOrderEntityToOrder(OrderEntity orderEntity){
        Order order = new Order();
        order.setAddress(orderEntity.getAddress());
        order.setDate(orderEntity.getDate().toString());
        order.setPayment(orderEntity.getPayment());
        order.setStatus(orderEntity.getStatus());
        order.setId(orderEntity.getId());
        order.setDeliveryMethod(orderEntity.getDelivery());
        order.setCakes(orderEntity.getCakes().stream().collect(Collectors.toMap(e -> e.getCake().getId(), OrderCakeEntity::getCount)));
        User user = new User();
        UserEntity ue = orderEntity.getUser();
        user.setId(ue.getId());
        user.setNumber(ue.getNumber());
        user.setName(ue.getName());
        user.setSurname(ue.getSurname());
        order.setUser(user);
        return order;
    }

    private <T> Set<T> setDifference(Set<T> set1, Set<T> set2){
        Set<T> diff = new HashSet<>(set1);
        diff.removeAll(set2);
        return diff;
    }
}
