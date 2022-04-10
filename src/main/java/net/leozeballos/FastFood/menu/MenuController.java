package net.leozeballos.FastFood.menu;

import net.leozeballos.FastFood.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MenuController {

    private final MenuService menuService;
    private final ProductService productService;

    @Autowired
    public MenuController(MenuService menuService, ProductService productService) {
        this.menuService = menuService;
        this.productService = productService;
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

    @RequestMapping("/menu/edit/{id}")
    public ModelAndView editMenu(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("edit_menu");
        mav.addObject("menu", menuService.findById(id));
        mav.addObject("productsList", productService.findAll());
        mav.addObject("pageTitle", "Edit Menu");
        return mav;
    }

    @RequestMapping("/menu/new")
    public String newMenu(Model model) {
        Menu menu = new Menu();
        model.addAttribute("menu", menu);
        model.addAttribute("productsList", productService.findAll());
        model.addAttribute("pageTitle", "New Menu");
        return "new_menu";
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
        return "list_menu";
    }

}
