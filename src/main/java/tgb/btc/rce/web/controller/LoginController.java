package tgb.btc.rce.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tgb.btc.rce.enums.RoleConstants;
import tgb.btc.rce.repository.WebUserRepository;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    private WebUserRepository webUserRepository;

    @Autowired
    public void setWebUserRepository(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

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
    public ObjectNode loginSuccess(Principal principal) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("loginSuccess", true);
        List<RoleConstants> roles = webUserRepository.getRolesByUsername(principal.getName()).stream()
                .map(role -> RoleConstants.valueOf(role.getName()))
                .collect(Collectors.toList());
        if (roles.stream().anyMatch(RoleConstants.ROLE_ADMIN::equals)) {
            objectNode.put("loginUrl", "/web/main");
        } else {
            objectNode.put("loginUrl", "/");
        }
        return objectNode;
    }
}
