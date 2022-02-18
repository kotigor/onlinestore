package com.konstantinov.onlinestore.bd.orders;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("SELECT orders FROM OrderEntity orders " +
            "JOIN orders.user user " +
            "WHERE user.number = :number")
    List<OrderEntity> ffind_Number(@Param("number") String number, Pageable page);

}
