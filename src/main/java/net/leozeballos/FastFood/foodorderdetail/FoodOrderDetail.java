package net.leozeballos.FastFood.foodorderdetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.leozeballos.FastFood.item.Item;
import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class FoodOrderDetail {

    /**
     * Unique identifier for the food order detail.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The price of the item that is being ordered at the time of the order.
     * Equals item.calculatePrice() at the time of the order.
     */
    @Column(nullable = false, precision = 2, updatable = false)
    private double historicPrice;

    /**
     * The quantity of the item that is being ordered.
     */
    @Column(nullable = false)
    @ColumnDefault(value = "1")
    @Min(value = 1)
    @Max(value = 99)
    private Integer quantity;

    /**
     * The item that is being ordered. Can be a product or a menu.
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    private Item item;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Calculates the subtotal of the order detail.
     * @return double The subtotal price of the order detail that is being ordered.
     */
    public double calculateSubtotal() {
        if (historicPrice == 0) {
            this.setHistoricPrice(item.calculatePrice());
        }
        return historicPrice * quantity;
    }

}
