package springboot.springbootweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * end point 찾는 우선순위
     * 1. Spring Controller
     * 2. static
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
