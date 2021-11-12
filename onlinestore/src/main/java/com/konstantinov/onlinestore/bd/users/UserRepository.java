package com.konstantinov.onlinestore.bd.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByNumber(String number);
}
