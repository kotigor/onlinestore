package com.konstantinov.onlinestore.bd.users;

import com.konstantinov.onlinestore.rest.dto.User;

import java.util.List;

public interface UserService {
    public User getUserById(Long id);
    public User getOrCreateUser(User user);
    public List<User> getSomeUsers(Integer page);
    public void updateUser(User user);
    public User getUserByNumber(String number);
}
