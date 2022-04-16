package net.leozeballos.FastFood.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Address {

    /**
     * The unique identifier for this address.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The street address.
     */
    @Column(nullable = false, length = 100)
    private String street;

    /**
     * The city.
     */
    @Column(nullable = false, length = 100)
    private String city;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
}