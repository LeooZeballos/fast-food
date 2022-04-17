package net.leozeballos.FastFood.foodorderdetail;

import net.leozeballos.FastFood.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FoodOrderDetailServiceTest {

    @Mock
    private FoodOrderDetailRepository foodOrderDetailRepository;
    private FoodOrderDetailService underTest;

    @BeforeEach
    void setUp() {
        underTest = new FoodOrderDetailService(foodOrderDetailRepository);
    }

    @Test
    void canFindAllFoodOrderDetails() {
        // when
        underTest.findAll();

        // then
        verify(foodOrderDetailRepository).findAll();
    }

    @Test
    void canFindFoodOrderDetailById() {
        // given
        Long id = 1L;

        // when
        underTest.findById(id);

        // then
        verify(foodOrderDetailRepository).findById(id);
    }

    @Test
    void canSaveFoodOrderDetail() {
        // given
        FoodOrderDetail foodOrderDetail = new FoodOrderDetail();

        // when
        underTest.save(foodOrderDetail);

        // then
        verify(foodOrderDetailRepository).save(foodOrderDetail);
    }

    @Test
    void canSaveFoodOrderDetailWhenItemNotNull() {
        // given
        FoodOrderDetail foodOrderDetail =  FoodOrderDetail.builder()
                .item(Product.builder().price(10.0).build())
                .build();

        // when
        underTest.save(foodOrderDetail);

        // then
        ArgumentCaptor<FoodOrderDetail> captor = ArgumentCaptor.forClass(FoodOrderDetail.class);
        verify(foodOrderDetailRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(foodOrderDetail);
    }

    @Test
    void canDeleteFoodOrderDetail() {
        // given
        FoodOrderDetail foodOrderDetail = new FoodOrderDetail();

        // when
        underTest.delete(foodOrderDetail);

        // then
        ArgumentCaptor<FoodOrderDetail> captor = ArgumentCaptor.forClass(FoodOrderDetail.class);
        verify(foodOrderDetailRepository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(foodOrderDetail);
    }

    @Test
    void canDeleteFoodOrderDetailById() {
        // given
        Long id = 1L;

        // when
        underTest.deleteById(id);

        // then
        verify(foodOrderDetailRepository).deleteById(id);
    }

    @Test
    void canDeleteAllFoodOrderDetails() {
        // when
        underTest.deleteAll();

        // then
        verify(foodOrderDetailRepository).deleteAll();
    }
}