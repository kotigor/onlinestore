package com.konstantinov.onlinestore.bd.users;

import com.konstantinov.onlinestore.bd.orders.OrderEntity;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
@RequiredArgsConstructor
@Table(name = "USER_INFO")
public class UserEntity {
    @Setter(AccessLevel.NONE)
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @Column(unique = true, nullable = false)
    @NaturalId(mutable = true)
    private String number;

    private String name;

    private String surname;

    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy = "user")
    private List<OrderEntity> orders = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserEntity that = (UserEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
