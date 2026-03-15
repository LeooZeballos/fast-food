package net.leozeballos.FastFood.foodorderdetail;

import net.leozeballos.FastFood.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodOrderDetailService {

    private final FoodOrderDetailRepository foodOrderDetailRepository;

    public FoodOrderDetailService(FoodOrderDetailRepository foodOrderDetailRepository) {
        this.foodOrderDetailRepository = foodOrderDetailRepository;
    }

    public List<FoodOrderDetail> findAll() {
        return foodOrderDetailRepository.findAll();
    }

    public FoodOrderDetail findById(Long id) {
        return foodOrderDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodOrderDetail not found with id: " + id));
    }

    public FoodOrderDetail save(FoodOrderDetail foodOrderDetail) {
        if (foodOrderDetail.getHistoricPrice() == 0 && foodOrderDetail.getItem() != null) {
            foodOrderDetail.setHistoricPrice(foodOrderDetail.getItem().calculatePrice());
        }
        return foodOrderDetailRepository.save(foodOrderDetail);
    }

    public void delete(FoodOrderDetail foodOrderDetail) {
        foodOrderDetailRepository.delete(foodOrderDetail);
    }

    public void deleteById(Long id) {
        if (!foodOrderDetailRepository.existsById(id)) {
            throw new ResourceNotFoundException("FoodOrderDetail not found with id: " + id);
        }
        foodOrderDetailRepository.deleteById(id);
    }

    public void deleteAll() {
        foodOrderDetailRepository.deleteAll();
    }

}
