package com.konstantinov.onlinestore.bd.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompositionRepository extends JpaRepository<CompositionEntity, Long> {
    @Query("SELECT Composition FROM CompositionEntity Composition WHERE Composition.name = :NAME")
    CompositionEntity ffindByName(@Param("NAME") String name);
}
