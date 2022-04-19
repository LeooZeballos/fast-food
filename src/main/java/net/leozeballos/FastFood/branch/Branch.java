package net.leozeballos.FastFood.branch;

import lombok.*;
import net.leozeballos.FastFood.address.Address;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Address address;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
