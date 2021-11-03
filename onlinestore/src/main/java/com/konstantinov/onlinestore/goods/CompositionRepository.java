package com.konstantinov.onlinestore.goods;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompositionRepository extends JpaRepository<CompositionEntity, Long> {
    CompositionEntity findByName(String name);
}
