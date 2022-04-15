package net.leozeballos.FastFood.foodorder;

import net.leozeballos.FastFood.branch.Branch;
import net.leozeballos.FastFood.branch.BranchService;
import net.leozeballos.FastFood.foodorderdetail.FoodOrderDetail;
import net.leozeballos.FastFood.foodorderstatemachine.FoodOrderState;
import net.leozeballos.FastFood.item.Item;
import net.leozeballos.FastFood.menu.Menu;
import net.leozeballos.FastFood.menu.MenuService;
import net.leozeballos.FastFood.product.Product;
import net.leozeballos.FastFood.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

@Controller
public class FoodOrderController {

    private final FoodOrderService foodOrderService;
    private final BranchService branchService;
    private final MenuService menuService;
    private final ProductService productService;

    @Autowired
    public FoodOrderController(FoodOrderService foodOrderService, BranchService branchService, MenuService menuService, ProductService productService) {
        this.foodOrderService = foodOrderService;
        this.branchService = branchService;
        this.menuService = menuService;
        this.productService = productService;
    }

    @ModelAttribute("branchesList")
    public List<Branch> getBranches() {
        return branchService.findAll();
    }

    @ModelAttribute("itemsList")
    public List<Item> getItems() {
        return populateItems();
    }

/*    @RequestMapping("/food_order/list")
    public String listFoodOrders(Model model) {
        model.addAttribute("listFoodOrders", foodOrderService.findAll());
        model.addAttribute("pageTitle", "Food Orders List");
        return "food_order/list_order";
    }*/

    @RequestMapping(value="/food_order/list", params={"type"})
    public String listFoodOrdersInPreparation(Model model, @Param("type") String type) {
        if (model != null) {
            if (type.equals("created")) {
                model.addAttribute("listFoodOrders", foodOrderService.findByState(FoodOrderState.CREATED));
            }else if (type.equals("in_preparation")) {
                model.addAttribute("listFoodOrders", foodOrderService.findByState(FoodOrderState.INPREPARATION));
            } else if (type.equals("finished")) {
                model.addAttribute("listFoodOrders", foodOrderService.findByState(FoodOrderState.DONE));
            } else if (type.equals("all")) {
                model.addAttribute("listFoodOrders", foodOrderService.findAll());
            } else {
                model.addAttribute("listFoodOrders", new ArrayList<FoodOrder>());
            }
        }
        model.addAttribute("pageTitle", "Orders In Preparation");
        return "food_order/list_order";
    }

    @RequestMapping("/food_order/order")
    public String newFoodOrder(Model model) {
        FoodOrder foodOrder = new FoodOrder();
        foodOrder.getFoodOrderDetails().add(new FoodOrderDetail());
        model.addAttribute("foodOrder", foodOrder);
        model.addAttribute("pageTitle", "New Food Order");
        return "food_order/order";
    }

    @RequestMapping(value="/food_order/order", params={"addItem"})
    public String addItem(final FoodOrder foodOrder, final BindingResult bindingResult) {
        foodOrder.getFoodOrderDetails().add(new FoodOrderDetail());
        return "food_order/order";
    }

    @RequestMapping(value="/food_order/order", params={"removeItem"})
    public String removeItem(@ModelAttribute final FoodOrder foodOrder, final BindingResult bindingResult, final HttpServletRequest req) {
        if (foodOrder.getFoodOrderDetails().size() > 1) {
            final Integer rowId = Integer.valueOf(req.getParameter("removeItem"));
            foodOrder.getFoodOrderDetails().remove(rowId.intValue());
        } else {
            bindingResult.reject("error.foodOrder", "You must have at least one item");
        }
        return "food_order/order";
    }

    @RequestMapping(value="/food_order/order", params={"save"})
    public String saveFoodOrder(@ModelAttribute("foodOrder") FoodOrder foodOrder) throws RuntimeException {
        // if the foodOrder is new, set the status to "new"
        if (foodOrder.getState() == null) {
            foodOrder.setState(FoodOrderState.CREATED);
        }
        // save the foodOrder
        if (foodOrderService.save(foodOrder) != null) {
            return "redirect:/food_order/list";
        } else {
            throw new RuntimeException("Error saving order");
        }
    }

    public List<Item> populateItems() {
        ArrayList<Item> itemsList = new ArrayList<>();
        itemsList.addAll(productService.findAll());
        itemsList.addAll(menuService.findAll());
        return itemsList;
    }

}
