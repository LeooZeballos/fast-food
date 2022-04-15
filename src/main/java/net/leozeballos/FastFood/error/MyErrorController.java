package net.leozeballos.FastFood.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("WrapperTypeMayBePrimitive")
@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {

            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error_status", "404");
                model.addAttribute("error_message", "Page not found");
                model.addAttribute("error_description", "The page you are looking for is not found");
            } else {
                model.addAttribute("error_status", "500");
                model.addAttribute("error_message", "Internal Server Error");
                model.addAttribute("error_description", "The server encountered an internal error");
            }

        }

        model.addAttribute("pageTitle", "Error");

        return "error";
    }

}
