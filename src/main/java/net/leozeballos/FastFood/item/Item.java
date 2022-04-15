package net.leozeballos.FastFood.item;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = javax.persistence.InheritanceType.TABLE_PER_CLASS)
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    /**
     * The name of the item. String of length 1 to 50. Must be unique.
     */
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    public double calculatePrice(){
        return 0;
    }

}