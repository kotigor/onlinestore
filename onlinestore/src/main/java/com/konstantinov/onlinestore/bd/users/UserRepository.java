package com.konstantinov.onlinestore.bd.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByNumber(String number);
}
