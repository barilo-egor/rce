package tgb.btc.rce.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MaimController {

    @GetMapping("/web")
    public String web() {
        return "main";
    }
}
