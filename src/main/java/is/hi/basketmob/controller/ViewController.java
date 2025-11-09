package is.hi.basketmob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "index";
    }

    @GetMapping("/games")
    public String games(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "games";
    }

    @GetMapping("/standings")
    public String standings(Model model) {
        return "standings";
    }
}
