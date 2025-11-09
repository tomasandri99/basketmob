package is.hi.basketmob.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class RootController {
    @GetMapping("/swagger")
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
