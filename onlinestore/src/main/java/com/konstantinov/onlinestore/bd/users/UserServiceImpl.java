package com.konstantinov.onlinestore.bd.users;

import com.konstantinov.onlinestore.rest.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
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
    public void addUser(User user) {

    }
}
