package tgb.btc.rce.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/q")
public class TestController {

    @GetMapping("/w")
    public String get() {
        return "index";
    }
}
