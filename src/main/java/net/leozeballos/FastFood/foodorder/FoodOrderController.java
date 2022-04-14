package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.branch.BranchService;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetailCreationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FoodOrderController {

    private final FoodOrderService foodOrderService;
    private final BranchService branchService;

    @Autowired
    public FoodOrderController(FoodOrderService foodOrderService, BranchService branchService) {
        this.foodOrderService = foodOrderService;
        this.branchService = branchService;
    }

    @RequestMapping("/food_order/list")
    public String listFoodOrders(Model model) {
        model.addAttribute("listFoodOrders", foodOrderService.findAll());
        model.addAttribute("pageTitle", "Food Orders List");
        return "food_order/list_order";
    }

    @RequestMapping("/food_order/new")
    public String newFoodOrder(Model model) {
        FoodOrder foodOrder = new FoodOrder();
        foodOrder.addFoodOrderDetail(new FoodOrderDetail());
        model.addAttribute("foodOrder", foodOrder);
        model.addAttribute("branchesList", branchService.findAll());
        return "food_order/new_order";
    }

    /*@RequestMapping(value="/food_order/new", params={"addRow"})
    public String addRow(final SeedStarter seedStarter, final BindingResult bindingResult) {

        seedStarter.getRows().add(new Row());
        return "food_order/new_order";
    }

    @RequestMapping(value="/food_order/new", params={"removeRow"})
    public String removeRow(
            final SeedStarter seedStarter, final BindingResult bindingResult,
            final HttpServletRequest req) {
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));
        seedStarter.getRows().remove(rowId.intValue());
        return "food_order/new_order";
    }*/

    @RequestMapping("/food_order/save")
    public String saveFoodOrder(@ModelAttribute("foodOrder") FoodOrder foodOrder) throws RuntimeException {
        // if the foodOrder is new, set the status to "new"
        if (foodOrder.getState() == null) {
            foodOrderService.newFoodOrder(foodOrder);
        }
        // save the foodOrder
        if (foodOrderService.save(foodOrder) != null) {
            return "redirect:/food_order/list_order";
        } else {
            throw new RuntimeException("Error saving order");
        }
    }


}
