package net.leozeballos.FastFood.branch;

import lombok.*;
import net.leozeballos.FastFood.address.Address;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Branch {

    /**
     * The unique identifier of the branch.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the branch.
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * The address of the branch.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Branch branch = (Branch) o;
        return id != null && Objects.equals(id, branch.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
