package net.leozeballos.FastFood.foodorderdetail;

import java.util.ArrayList;
import java.util.List;

public class FoodOrderDetailCreationDto {

    private List<FoodOrderDetail> foodOrderDetails;

    public FoodOrderDetailCreationDto() {
        this.foodOrderDetails = new ArrayList<>();
    }

    public FoodOrderDetailCreationDto(List<FoodOrderDetail> foodOrderDetails) {
        this.foodOrderDetails = foodOrderDetails;
    }

    public List<FoodOrderDetail> getFoodOrderDetails() {
        return foodOrderDetails;
    }

    public void setFoodOrderDetails(List<FoodOrderDetail> foodOrderDetails) {
        this.foodOrderDetails = foodOrderDetails;
    }

    public void addFoodOrderDetail(FoodOrderDetail foodOrderDetail) {
        this.foodOrderDetails.add(foodOrderDetail);
    }

}
