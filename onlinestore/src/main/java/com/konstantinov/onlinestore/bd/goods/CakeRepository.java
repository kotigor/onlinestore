package com.konstantinov.onlinestore.bd.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

public interface CakeRepository extends JpaRepository<CakeEntity, Long> {
    CakeEntity getById(Long id);

    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "0")})
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    CakeEntity getFirstBy();
}
