package net.leozeballos.FastFood.foodorderdetail;

import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.product.Product;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FoodOrderDetailTest {

    @Test
    void shouldCreateFoodOrderDetailWithEmptyConstructor() {
        // when
        FoodOrderDetail foodOrderDetail = new FoodOrderDetail();

        // then
        assertThat(foodOrderDetail).isNotNull();
    }

    @Test
    void shouldCreateFoodOrderDetailWithConstructor() {
        // when
        FoodOrderDetail foodOrderDetail = new FoodOrderDetail(1L, 0.0, 1, Product.builder().build());

        // then
        assertThat(foodOrderDetail).isNotNull();
        assertThat(foodOrderDetail.getId()).isEqualTo(1L);
        assertThat(foodOrderDetail.getHistoricPrice()).isEqualTo(0.0);
        assertThat(foodOrderDetail.getQuantity()).isEqualTo(1);
        assertThat(foodOrderDetail.getItem()).isNotNull();
    }

    @Test
    void shouldCreateFoodOrderDetailWithBuilder() {
        // when
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder()
                .id(1L)
                .item(Product.builder().build())
                .historicPrice(10.0)
                .quantity(2)
                .build();

        // then
        assertThat(foodOrderDetail).isNotNull();
        assertThat(foodOrderDetail.getId()).isEqualTo(1L);
        assertThat(foodOrderDetail.getHistoricPrice()).isEqualTo(10.0);
        assertThat(foodOrderDetail.getQuantity()).isEqualTo(2);
        assertThat(foodOrderDetail.getItem()).isNotNull();
    }

    @Test
    void testHashCode() {
        // given
        FoodOrderDetail foodOrderDetail1 = new FoodOrderDetail();
        FoodOrderDetail foodOrderDetail2 = new FoodOrderDetail();

        // when
        int hashCode1 = foodOrderDetail1.hashCode();
        int hashCode2 = foodOrderDetail2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void calculateSubtotal() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder()
                .historicPrice(10.0)
                .quantity(2)
                .item(Product.builder().price(10.0).build())
                .build();

        // when
        double subtotal = foodOrderDetail.calculateSubtotal();

        // then
        assertThat(subtotal).isEqualTo(20.0);
    }

    @Test
    void checkIfHistoricPriceIsSetWhenCalculatingSubtotal() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder()
                .quantity(2)
                .item(Product.builder().price(10.0).build())
                .build();

        // when
        double subtotal = foodOrderDetail.calculateSubtotal();

        // then
        assertThat(subtotal).isEqualTo(20.0);
    }

    @Test
    void getId() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().id(1L).build();

        // when
        Long id = foodOrderDetail.getId();

        // then
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void getHistoricPrice() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().historicPrice(10.0).build();

        // when
        double historicPrice = foodOrderDetail.getHistoricPrice();

        // then
        assertThat(historicPrice).isEqualTo(10.0);
    }

    @Test
    void getQuantity() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().quantity(2).build();

        // when
        int quantity = foodOrderDetail.getQuantity();

        // then
        assertThat(quantity).isEqualTo(2);
    }

    @Test
    void getItem() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().item(Product.builder().build()).build();

        // when
        Item item = foodOrderDetail.getItem();

        // then
        assertThat(item).isNotNull();
    }

    @Test
    void setId() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().build();

        // when
        foodOrderDetail.setId(1L);

        // then
        assertThat(foodOrderDetail.getId()).isEqualTo(1L);
    }

    @Test
    void setHistoricPrice() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().build();

        // when
        foodOrderDetail.setHistoricPrice(10.0);

        // then
        assertThat(foodOrderDetail.getHistoricPrice()).isEqualTo(10.0);
    }

    @Test
    void setQuantity() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().build();

        // when
        foodOrderDetail.setQuantity(2);

        // then
        assertThat(foodOrderDetail.getQuantity()).isEqualTo(2);
    }

    @Test
    void setItem() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder().build();

        // when
        foodOrderDetail.setItem(Product.builder().build());

        // then
        assertThat(foodOrderDetail.getItem()).isNotNull();
    }

    @Test
    void testToString() {
        // given
        FoodOrderDetail foodOrderDetail = FoodOrderDetail.builder()
                .historicPrice(10.0)
                .quantity(2)
                .item(Product.builder().build())
                .build();

        // when
        String actual = foodOrderDetail.toString();

        // then
        String expected = "FoodOrderDetail(id=null, historicPrice=10.0, quantity=2, item=Product{name='null', price=0.0})";
        assertThat(actual).isEqualTo(expected);
    }

}