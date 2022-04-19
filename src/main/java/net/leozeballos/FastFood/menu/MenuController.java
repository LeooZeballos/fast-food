package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.product.Product;
import net.leozeballos.FastFood.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class MenuController {

    private final MenuService menuService;
    private final ProductService productService;

    @Autowired
    public MenuController(MenuService menuService, ProductService productService) {
        this.menuService = menuService;
        this.productService = productService;
    }

    @ModelAttribute("productsList")
    public List<Product> getProducts() {
        return populateProducts();
    }

    public List<Product> populateProducts() {
        ArrayList<Product> productsList = new ArrayList<>(productService.findAll());
        // remove disabled items
        productsList.removeIf(product -> !product.isActive());
        return productsList;
    }

    @GetMapping(value = "/menu/{id}", produces = "application/json")
    public @ResponseBody
    Menu getMenu(@PathVariable("id") Long id) {
        return menuService.findById(id);
    }

    @RequestMapping("/menu/delete/{id}")
    public String deleteMenu(@PathVariable("id") Long id) {
        menuService.deleteById(id);
        return "redirect:/menu/list";
    }

    @RequestMapping("/menu/disable/{id}")
    public String disableMenu(@PathVariable("id") Long id) {
        menuService.disableItem(id);
        return "redirect:/menu/list";
    }

    @RequestMapping("/menu/enable/{id}")
    public String enableMenu(@PathVariable("id") Long id) {
        menuService.enableItem(id);
        return "redirect:/menu/list";
    }

    @RequestMapping("/menu/edit/{id}")
    public ModelAndView editMenu(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("menu/edit_menu");
        mav.addObject("menu", menuService.findById(id));
        mav.addObject("pageTitle", "Edit Menu");
        return mav;
    }

    @RequestMapping("/menu/new")
    public String newMenu(Model model) {
        Menu menu = new Menu();
        model.addAttribute("menu", menu);
        model.addAttribute("pageTitle", "New Menu");
        return "menu/new_menu";
    }

    @RequestMapping("/menu/save")
    public String saveMenu(@ModelAttribute("menu") Menu menu) throws RuntimeException {
        if (menuService.save(menu) != null) {
            return "redirect:/menu/list";
        } else {
            throw new RuntimeException("Error saving menu");
        }
    }

    @RequestMapping("/menu/list")
    public String listMenus(Model model) {
        model.addAttribute("listMenus", menuService.findAll());
        model.addAttribute("pageTitle", "Menus List");
        return "menu/list_menu";
    }

}
