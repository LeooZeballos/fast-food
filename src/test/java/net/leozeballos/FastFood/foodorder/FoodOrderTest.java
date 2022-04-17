package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.product.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class FoodOrderTest {

    @Test
    void shouldCreateFoodOrderWithEmptyConstructor() {
        // when
        FoodOrder foodOrder = new FoodOrder();

        // then
        assertThat(foodOrder).isNotNull();
    }

    @Test
    void shouldCreateFoodOrderWithBuilder() {
        // when
        FoodOrder foodOrder = FoodOrder.builder()
                .id(1L)
                .branch(Branch.builder().build())
                .creationTimestamp(LocalDateTime.now())
                .paymentTimestamp(LocalDateTime.now().plus(20, java.time.temporal.ChronoUnit.MINUTES))
                .state(FoodOrderState.PAID)
                .foodOrderDetails(new ArrayList<>())
                .build();

        // then
        assertThat(foodOrder).isNotNull();
        assertThat(foodOrder.getId()).isEqualTo(1L);
        assertThat(foodOrder.getBranch()).isNotNull();
        assertThat(foodOrder.getCreationTimestamp()).isNotNull();
        assertThat(foodOrder.getPaymentTimestamp()).isNotNull();
        assertThat(foodOrder.getState()).isEqualTo(FoodOrderState.PAID);
        assertThat(foodOrder.getFoodOrderDetails()).isNotNull();
    }

    @Test
    void testHashCode() {
        // given
        FoodOrder foodOrder1 = FoodOrder.builder().build();
        FoodOrder foodOrder2 = FoodOrder.builder().build();

        // when
        int hashCode1 = foodOrder1.hashCode();
        int hashCode2 = foodOrder2.hashCode();

        // then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void calculateTotal() {
        // given
        FoodOrder foodOrder = FoodOrder.builder()
                .foodOrderDetails(new ArrayList<>())
                .build();
        FoodOrderDetail foodOrderDetail1 = FoodOrderDetail.builder().quantity(2).item(Product.builder().price(10.0).build()).build();
        FoodOrderDetail foodOrderDetail2 = FoodOrderDetail.builder().quantity(3).item(Product.builder().price(20.0).build()).build();
        foodOrder.getFoodOrderDetails().add(foodOrderDetail1);
        foodOrder.getFoodOrderDetails().add(foodOrderDetail2);

        // when
        double total = foodOrder.calculateTotal();

        // then
        assertThat(total).isEqualTo(80.0);
    }

    @Test
    void getFormattedCreationTimestamp() {
        // given
        LocalDateTime creationTimestamp = LocalDateTime.now();
        FoodOrder foodOrder = FoodOrder.builder().creationTimestamp(creationTimestamp).build();

        // when
        String actual = foodOrder.getFormattedCreationTimestamp();

        // then
        String expected = creationTimestamp.format(FoodOrder.DATE_TIME_FORMATTER);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getFormattedPaymentTimestamp() {
        // given
        LocalDateTime paymentTimestamp = LocalDateTime.now();
        FoodOrder foodOrder = FoodOrder.builder().paymentTimestamp(paymentTimestamp).build();

        // when
        String actual = foodOrder.getFormattedPaymentTimestamp();

        // then
        String expected = paymentTimestamp.format(FoodOrder.DATE_TIME_FORMATTER);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getFormattedFoodOrderDetails() {
        // given
        FoodOrder foodOrder = FoodOrder.builder()
                .foodOrderDetails(new ArrayList<>())
                .build();
        FoodOrderDetail foodOrderDetail1 = FoodOrderDetail.builder().quantity(2).item(Product.builder().price(10.0).build()).build();
        FoodOrderDetail foodOrderDetail2 = FoodOrderDetail.builder().quantity(3).item(Product.builder().price(20.0).build()).build();
        foodOrderDetail1.getItem().setName("name1");
        foodOrderDetail2.getItem().setName("name2");
        foodOrder.getFoodOrderDetails().add(foodOrderDetail1);
        foodOrder.getFoodOrderDetails().add(foodOrderDetail2);

        // when
        String actual = foodOrder.getFormattedFoodOrderDetails();

        // then
        String expected = "2 x name1, 3 x name2";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getFormattedTotal() {
        // given
        FoodOrder foodOrder = FoodOrder.builder()
                .foodOrderDetails(new ArrayList<>())
                .build();
        FoodOrderDetail foodOrderDetail1 = FoodOrderDetail.builder().quantity(2).item(Product.builder().price(10.0).build()).build();
        FoodOrderDetail foodOrderDetail2 = FoodOrderDetail.builder().quantity(3).item(Product.builder().price(20.0).build()).build();
        foodOrder.getFoodOrderDetails().add(foodOrderDetail1);
        foodOrder.getFoodOrderDetails().add(foodOrderDetail2);

        // when
        String actual = foodOrder.getFormattedTotal();

        // then
        String expected = "$80,00";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getFormattedState() {
        // given
        FoodOrder foodOrder = FoodOrder.builder()
                .state(FoodOrderState.CREATED)
                .build();

        // when
        String actual = foodOrder.getFormattedState();

        // then
        String expected = "Created";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getId() {
        // given
        FoodOrder foodOrder = FoodOrder.builder().id(1L).build();

        // when
        Long actual = foodOrder.getId();

        // then
        Long expected = 1L;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCreationTimestamp() {
        // given
        LocalDateTime creationTimestamp = LocalDateTime.now();
        FoodOrder foodOrder = FoodOrder.builder().creationTimestamp(creationTimestamp).build();

        // when
        LocalDateTime actual = foodOrder.getCreationTimestamp();

        // then
        assertThat(actual).isEqualTo(creationTimestamp);
    }

    @Test
    void getPaymentTimestamp() {
        // given
        LocalDateTime paymentTimestamp = LocalDateTime.now();
        FoodOrder foodOrder = FoodOrder.builder().paymentTimestamp(paymentTimestamp).build();

        // when
        LocalDateTime actual = foodOrder.getPaymentTimestamp();

        // then
        assertThat(actual).isEqualTo(paymentTimestamp);
    }

    @Test
    void getState() {
        // given
        FoodOrder foodOrder = FoodOrder.builder().state(FoodOrderState.CREATED).build();

        // when
        FoodOrderState actual = foodOrder.getState();

        // then
        FoodOrderState expected = FoodOrderState.CREATED;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getBranch() {
        // given
        Branch branch = Branch.builder().id(1L).build();
        FoodOrder foodOrder = FoodOrder.builder().branch(branch).build();

        // when
        Branch actual = foodOrder.getBranch();

        // then
        assertThat(actual).isEqualTo(branch);
    }

    @Test
    void getFoodOrderDetails() {
        // given
        List<FoodOrderDetail> foodOrderDetails = new ArrayList<>();
        FoodOrderDetail foodOrderDetail1 = FoodOrderDetail.builder().quantity(2).item(Product.builder().price(10.0).build()).build();
        FoodOrderDetail foodOrderDetail2 = FoodOrderDetail.builder().quantity(3).item(Product.builder().price(20.0).build()).build();
        foodOrderDetails.add(foodOrderDetail1);
        foodOrderDetails.add(foodOrderDetail2);
        FoodOrder foodOrder = FoodOrder.builder().foodOrderDetails(foodOrderDetails).build();

        // when
        List<FoodOrderDetail> actual = foodOrder.getFoodOrderDetails();

        // then
        assertThat(actual).isEqualTo(foodOrderDetails);
    }

    @Test
    void setId() {
        // given
        Long expected = 1L;
        FoodOrder foodOrder = FoodOrder.builder().build();

        // when
        foodOrder.setId(expected);

        // then
        Long actual = foodOrder.getId();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void setCreationTimestamp() {
        // given
        LocalDateTime creationTimestamp = LocalDateTime.now();
        FoodOrder foodOrder = FoodOrder.builder().build();

        // when
        foodOrder.setCreationTimestamp(creationTimestamp);

        // then
        LocalDateTime actual = foodOrder.getCreationTimestamp();
        assertThat(actual).isEqualTo(creationTimestamp);
    }

    @Test
    void setPaymentTimestamp() {
        // given
        LocalDateTime paymentTimestamp = LocalDateTime.now();
        FoodOrder foodOrder = FoodOrder.builder().build();

        // when
        foodOrder.setPaymentTimestamp(paymentTimestamp);

        // then
        LocalDateTime actual = foodOrder.getPaymentTimestamp();
        assertThat(actual).isEqualTo(paymentTimestamp);
    }

    @Test
    void setState() {
        // given
        FoodOrderState state = FoodOrderState.CREATED;
        FoodOrder foodOrder = FoodOrder.builder().build();

        // when
        foodOrder.setState(state);

        // then
        FoodOrderState actual = foodOrder.getState();
        assertThat(actual).isEqualTo(state);
    }

    @Test
    void setBranch() {
        // given
        Branch branch = Branch.builder().id(1L).build();
        FoodOrder foodOrder = FoodOrder.builder().build();

        // when
        foodOrder.setBranch(branch);

        // then
        Branch actual = foodOrder.getBranch();
        assertThat(actual).isEqualTo(branch);
    }

    @Test
    void setFoodOrderDetails() {
        // given
        List<FoodOrderDetail> foodOrderDetails = new ArrayList<>();
        FoodOrderDetail foodOrderDetail1 = FoodOrderDetail.builder().quantity(2).item(Product.builder().price(10.0).build()).build();
        FoodOrderDetail foodOrderDetail2 = FoodOrderDetail.builder().quantity(3).item(Product.builder().price(20.0).build()).build();
        foodOrderDetails.add(foodOrderDetail1);
        foodOrderDetails.add(foodOrderDetail2);
        FoodOrder foodOrder = FoodOrder.builder().build();

        // when
        foodOrder.setFoodOrderDetails(foodOrderDetails);

        // then
        List<FoodOrderDetail> actual = foodOrder.getFoodOrderDetails();
        assertThat(actual).isEqualTo(foodOrderDetails);
    }

    @Test
    void testToString() {
        // given
        FoodOrder foodOrder = FoodOrder.builder().build();

        // when
        String actual = foodOrder.toString();

        // then
        assertThat(actual).isEqualTo("FoodOrder(id=null, creationTimestamp=null, paymentTimestamp=null, state=null, branch=null)");
    }

}