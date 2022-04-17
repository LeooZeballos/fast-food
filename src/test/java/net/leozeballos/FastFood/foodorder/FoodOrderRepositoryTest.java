package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.address.Address;
import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchRepository;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class FoodOrderRepositoryTest {

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        branchRepository.deleteAll();
    }

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private FoodOrderRepository underTest;

    @Test
    void findByState() {
        // given
        Branch branch = branchRepository.save(Branch.builder()
                .name("Branch")
                .address(Address.builder().city("").street("").build())
                .build());
        underTest.save(FoodOrder.builder()
                .state(FoodOrderState.CREATED)
                .branch(branch)
                .build());
        underTest.save(FoodOrder.builder()
                .state(FoodOrderState.CREATED)
                .branch(branch)
                .build());
        underTest.save(FoodOrder.builder()
                .state(FoodOrderState.DONE)
                .branch(branch)
                .build());

        // when
        List<FoodOrder> result = underTest.findAllFoodOrdersByState(FoodOrderState.CREATED);

        // then
        for (FoodOrder foodOrder : result) {assertThat(foodOrder.getState()).isEqualTo(FoodOrderState.CREATED);}
        assertThat(result.size()).isEqualTo(2);
    }

}