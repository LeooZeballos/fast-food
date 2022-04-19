package net.leozeballos.FastFood.branch;

import lombok.*;
import net.leozeballos.FastFood.address.Address;

import javax.persistence.*;

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
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
