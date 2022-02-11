package com.konstantinov.onlinestore.bd.users;

import com.konstantinov.onlinestore.bd.goods.CakeEntity;
import com.konstantinov.onlinestore.bd.orders.OrderCakeEntity;
import com.konstantinov.onlinestore.rest.dto.Cake;
import com.konstantinov.onlinestore.rest.dto.Order;
import com.konstantinov.onlinestore.rest.dto.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    //private final UserRepository userRepository;

    private final UserDAO userRepository;

    @Autowired
    public UserServiceImpl(UserDAO userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long id) {
        UserEntity userEntity = userRepository.getById(id);
        return mapUserEntityToUser(userEntity, new User());
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

    @Override
    public List<User> getSomeUsers(Integer page){
        Pageable limit = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        //List<UserEntity> userEntities = userRepository.findAll(limit).toList();
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities
                .stream()
                .map(ue -> mapUserEntityToUser(ue, new User()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(User user) {
        UserEntity userEntity = userRepository.getById(user.getId());
        userEntity.setSurname(user.getSurname());
        userEntity.setName(user.getName());
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByNumber(String number) {
        UserEntity userEntity = userRepository.findByNumber(number);;
        return mapUserEntityToUser(userEntity, new User());
    }

    private User mapUserEntityToUser(UserEntity userEntity, User user){
        user.setId(userEntity.getId());
        user.setNumber(userEntity.getNumber());
        user.setName(userEntity.getName());
        user.setSurname(userEntity.getSurname());
        return user;
    }
}
