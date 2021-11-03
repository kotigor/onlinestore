package com.konstantinov.onlinestore.goods;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "CAKE")
public class CakeEntity {

    @Setter(AccessLevel.NONE)
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    private String name;

    private BigDecimal calories;

    private String image;

    private BigDecimal price;

    private BigDecimal weight;

    private String description;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "CAKE_COMPOSITION", joinColumns = @JoinColumn(name = "CAKE_ID", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "COMPOSITION_ID", referencedColumnName = "id"))
    private Set<CompositionEntity> composition = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CakeEntity that = (CakeEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
