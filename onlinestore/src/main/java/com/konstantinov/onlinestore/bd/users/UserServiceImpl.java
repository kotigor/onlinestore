package com.konstantinov.onlinestore.bd.users;

import com.konstantinov.onlinestore.bd.goods.CakeEntity;
import com.konstantinov.onlinestore.bd.orders.OrderCakeEntity;
import com.konstantinov.onlinestore.rest.dto.Cake;
import com.konstantinov.onlinestore.rest.dto.Order;
import com.konstantinov.onlinestore.rest.dto.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long id) {
        UserEntity userEntity = userRepository.getById(id);
        User user = new User();
        user.setId(userEntity.getId());
        user.setNumber(userEntity.getNumber());
        user.setName(userEntity.getName());
        user.setSurname(userEntity.getSurname());
        /*List<Order> orders = userEntity.getOrders().stream().map(o -> {
            Order newOrder = new Order();
            newOrder.setId(o.getId());
            Map<Long, Integer> cakes = o.getCakes().stream().collect(Collectors.toMap(OrderCakeEntity::getId, OrderCakeEntity::getCount));
            newOrder.setCakes(cakes);
            newOrder.setDate(o.getDate().toString());
            newOrder.setPayment(o.getPayment());
            newOrder.setStatus(o.getStatus());
            newOrder.setDeliveryMethod(o.getDelivery());
            return newOrder;
        }).collect(Collectors.toList());
        user.setOrders(orders);*/
        return user;
    }

    @Override
    public User getOrCreateUser(User user) {
        UserEntity userEntity = userRepository.findByNumber(user.getNumber());
        if(userEntity != null){
            user.setId(userEntity.getId());
            return user;
        }
        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setName(user.getName());
        newUserEntity.setNumber(user.getNumber());
        newUserEntity.setSurname(user.getSurname());
        Long id = userRepository.save(newUserEntity).getId();
        user.setId(id);
        return user;
    }
}
