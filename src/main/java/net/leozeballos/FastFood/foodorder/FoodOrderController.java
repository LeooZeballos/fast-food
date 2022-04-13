package net.leozeballos.FastFood.foodorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FoodOrderController {

    private final FoodOrderService foodOrderService;

    @Autowired
    public FoodOrderController(FoodOrderService foodOrderService) {
        this.foodOrderService = foodOrderService;
    }

    @RequestMapping("/food_order/list_order")
    public String listFoodOrders(Model model) {
        model.addAttribute("listFoodOrders", foodOrderService.findAll());
        model.addAttribute("pageTitle", "Food Orders List");
        return "food_order/list_order";
    }

    @RequestMapping("/food_order/new")
    public String newFoodOrder(Model model) {
        FoodOrder foodOrder = new FoodOrder();
        foodOrderService.newFoodOrder(foodOrder);
        model.addAttribute("foodOrder", foodOrder);
        model.addAttribute("pageTitle", "New Order");
        return "food_order/new_order";
    }

    @RequestMapping("/food_order/save")
    public String saveFoodOrder(@ModelAttribute("foodOrder") FoodOrder foodOrder) throws RuntimeException {
        if (foodOrderService.save(foodOrder) != null) {
            return "redirect:/food_order/list_order";
        } else {
            throw new RuntimeException("Error saving order");
        }
    }


}
