package tgb.btc.rce.web.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tgb.btc.rce.vo.web.RegistrationVO;

@Controller
@RequestMapping("/web/registration")
public class RegistrationController {

    @GetMapping("/init")
    public String init() {
        return "registration/registration";
    }

    @PostMapping("/registerUser")
    public ObjectNode registerUser(@RequestBody RegistrationVO registrationVO) {
        return null;
    }
}
