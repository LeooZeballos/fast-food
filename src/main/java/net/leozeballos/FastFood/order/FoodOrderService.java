package net.leozeballos.FastFood.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodOrderService {

    private final FoodOrderRepository foodOrderRepository;

    @Autowired
    public FoodOrderService(FoodOrderRepository foodOrderRepository) {
        this.foodOrderRepository = foodOrderRepository;
    }

    public List<FoodOrder> findAll() {
        return foodOrderRepository.findAll();
    }

    public FoodOrder findById(Long id) {
        return foodOrderRepository.findById(id).orElse(null);
    }

    public FoodOrder save(FoodOrder order) {
        return foodOrderRepository.save(order);
    }

    public void delete(FoodOrder order) {
        foodOrderRepository.delete(order);
    }

    public void deleteById(Long id) {
        foodOrderRepository.deleteById(id);
    }

    public void deleteAll() {
        foodOrderRepository.deleteAll();
    }

}
