package net.leozeballos.FastFood.item;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = javax.persistence.InheritanceType.TABLE_PER_CLASS)
public abstract class Item {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    public double calculatePrice(){
        return 0;
    }

}