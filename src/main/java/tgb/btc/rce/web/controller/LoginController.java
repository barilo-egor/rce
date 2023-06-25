package tgb.btc.rce.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/loginError")
    @ResponseBody
    public ObjectNode loginError() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("error", true);
        return objectNode;
    }

    @GetMapping("/loginSuccess")
    @ResponseBody
    public ObjectNode loginSuccess() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("loginSuccess", true);
        return objectNode;
    }
}
