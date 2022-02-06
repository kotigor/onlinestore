package com.konstantinov.onlinestore.bd.goods;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "COMPOSITION")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CompositionEntity {
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "composition", fetch = FetchType.LAZY)
    private Set<CakeEntity> cakes = new HashSet<>();

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @Column(unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompositionEntity that = (CompositionEntity) o;
        return name != null && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
