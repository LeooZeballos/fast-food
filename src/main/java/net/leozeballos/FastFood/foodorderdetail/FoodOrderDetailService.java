package net.leozeballos.FastFood.foodorderdetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodOrderDetailService {

    private final FoodOrderDetailRepository foodOrderDetailRepository;

    @Autowired
    public FoodOrderDetailService(FoodOrderDetailRepository foodOrderDetailRepository) {
        this.foodOrderDetailRepository = foodOrderDetailRepository;
    }

    public List<FoodOrderDetail> findAll() {
        return foodOrderDetailRepository.findAll();
    }

    public FoodOrderDetail findById(Long id) {
        return foodOrderDetailRepository.findById(id).orElse(null);
    }

    public FoodOrderDetail save(FoodOrderDetail foodOrderDetail) {
        return foodOrderDetailRepository.save(foodOrderDetail);
    }

    public void delete(FoodOrderDetail foodOrderDetail) {
        foodOrderDetailRepository.delete(foodOrderDetail);
    }

    public void deleteById(Long id) {
        foodOrderDetailRepository.deleteById(id);
    }

    public void deleteAll() {
        foodOrderDetailRepository.deleteAll();
    }

}