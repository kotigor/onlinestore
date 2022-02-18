package com.konstantinov.onlinestore.bd.orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    //@Query("SELECT Orders FROM OrderEntity Orders JOIN Orders.user User WHERE User.number=:NUMBER") //рабочий, написанный на паре вариант
    @Query("SELECT Order FROM OrderEntity Order INNER JOIN UserEntity User ON Order.user.id = User.id WHERE User.number=:NUMBER") //Так бы выглядел запрос, если бы не аннотации?
    List<OrderEntity> ffindByUser_Number(@Param("NUMBER") String number, Pageable page);

    @Query("SELECT Orders FROM OrderEntity Orders WHERE Orders.id=:ID")
    OrderEntity ggetById(@Param("ID") Long id);

    @Query("SELECT Orders FROM OrderEntity Orders")
    Page<OrderEntity> ffindAll(Pageable limit);
}
