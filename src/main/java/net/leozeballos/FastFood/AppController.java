package net.leozeballos.FastFood;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {
    @RequestMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("pageTitle", "Home");
        return "index";
    }
}
