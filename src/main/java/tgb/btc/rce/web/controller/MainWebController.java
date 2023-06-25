package tgb.btc.rce.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class MainWebController {

    @GetMapping("/main")
    public String web() {
        return "main";
    }
}
