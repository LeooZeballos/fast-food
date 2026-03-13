package net.leozeballos.FastFood.inventory;

import lombok.*;
import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.item.Item;

import jakarta.persistence.*;

/**
 * Represents the stock of an item in a specific branch.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"branch_id", "item_id"})
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    @Builder.Default
    private int stockQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAvailable = true;

    /**
     * Check if there's enough stock for a given quantity.
     * @param requestedQuantity The quantity requested.
     * @return boolean True if available and stock is sufficient.
     */
    public boolean hasStock(int requestedQuantity) {
        return isAvailable && stockQuantity >= requestedQuantity;
    }

    /**
     * Reduce stock by a given quantity.
     * @param quantity The quantity to reduce.
     */
    public void reduceStock(int quantity) {
        if (quantity > 0) {
            this.stockQuantity = Math.max(0, this.stockQuantity - quantity);
            if (this.stockQuantity == 0) {
                this.isAvailable = false;
            }
        }
    }
}
