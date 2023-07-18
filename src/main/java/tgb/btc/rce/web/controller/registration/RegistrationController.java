package tgb.btc.rce.web.controller.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.enums.RoleConstants;
import tgb.btc.rce.repository.WebUserRepository;
import tgb.btc.rce.service.impl.WebUserService;
import tgb.btc.rce.vo.web.CredentialsVO;

@Controller
@RequestMapping("/web/registration")
public class RegistrationController {

    private WebUserService webUserService;

    private WebUserRepository webUserRepository;

    @Autowired
    public void setWebUserRepository(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @Autowired
    public void setWebUserService(WebUserService webUserService) {
        this.webUserService = webUserService;
    }

    @GetMapping("/init")
    public String init() {
        return "registration/registration";
    }

    @PostMapping("/registerUser")
    @ResponseBody
    public ObjectNode registerUser(@RequestBody CredentialsVO credentialsVO, @RequestParam RoleConstants role) {
        webUserService.save(credentialsVO, role);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("success", true);
        return objectNode;
    }

    @GetMapping("/isUsernameFree")
    @ResponseBody
    public ObjectNode isUsernameFree(@RequestParam String username) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("result", webUserRepository.countByUsername(username) == 0);
        objectNode.put("success", true);
        return objectNode;
    }
}
