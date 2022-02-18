package com.konstantinov.onlinestore.bd.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT User FROM UserEntity User WHERE User.number=:NUMBER")
    UserEntity ffindByNumber(@Param("NUMBER") String number);

    @Query("SELECT User FROM UserEntity User WHERE User.id=:ID")
    UserEntity ggetById(@Param("ID") Long id);

    @Query("SELECT User FROM UserEntity User")
    Page<UserEntity> ffindAll(Pageable limit);
}
