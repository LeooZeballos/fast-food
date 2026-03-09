package net.leozeballos.FastFood.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the item. String of length 1 to 50. Must be unique.
     */
    @NotBlank(message = "Item name is required")
    @Size(min = 1, max = 50, message = "Item name must be between 1 and 50 characters")
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    /**
     * The icon identifier for the item (e.g., 'burger', 'drink').
     */
    @Column(length = 30)
    private String icon;

    /**
     * If the item is active.
     */
    @Column(nullable = false)
    private boolean isActive = true;

    public Item(String name) {
        this.name = name;
    }

    public double calculatePrice(){
        return 0;
    }

    /**
     * Disable the item.
     */
    public void disable() {
        this.isActive = false;
    }

    /**
     * Enable the item.
     */
    public void enable() {
        this.isActive = true;
    }
}
