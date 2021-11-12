package com.konstantinov.onlinestore.bd.goods;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CakeRepository extends JpaRepository<CakeEntity, Long> {
    CakeEntity getById(Long id);
}
