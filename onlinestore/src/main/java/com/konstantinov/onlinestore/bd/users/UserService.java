package com.konstantinov.onlinestore.bd.users;

import com.konstantinov.onlinestore.rest.dto.User;

public interface UserService {
    public User getUserById(Long id);
    public User getOrCreateUser(User user);
    public void addUser(User user);
}
