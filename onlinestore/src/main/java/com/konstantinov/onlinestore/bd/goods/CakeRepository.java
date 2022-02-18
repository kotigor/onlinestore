package com.konstantinov.onlinestore.bd.goods;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CakeRepository extends JpaRepository<CakeEntity, Long> {
    @Query("SELECT Cake FROM CakeEntity Cake WHERE Cake.id=:ID")
    CakeEntity ggetById(@Param("ID") Long id);

    @Query("SELECT Cake FROM CakeEntity Cake")
    Page<CakeEntity> ffindAll(Pageable limit);
}
