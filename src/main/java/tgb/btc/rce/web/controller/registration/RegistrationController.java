package tgb.btc.rce.web.controller.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.enums.RoleConstants;
import tgb.btc.rce.repository.WebUserRepository;
import tgb.btc.rce.service.impl.bean.WebUserService;
import tgb.btc.rce.vo.web.CredentialsVO;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/web/registration")
@Slf4j
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

    @GetMapping
    public String init(HttpServletRequest request, @RequestParam(required = false) Long chatId) {
        request.getSession().setAttribute("chatId", chatId);
        return "registration";
    }

    @PostMapping("/registerUser")
    @ResponseBody
    public ObjectNode registerUser(@RequestBody CredentialsVO credentialsVO, @RequestParam(required = false) RoleConstants role) {
        webUserService.save(credentialsVO.getUsername(), credentialsVO.getPassword(), role);
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

    @PostMapping("/changePassword")
    @ResponseBody
    public ObjectNode changePassword(@RequestParam String password, Principal principal) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        try {
            webUserService.changePassword(principal.getName(), password);
            objectNode.put("success", true);
        } catch (Exception e) {
            objectNode.put("success", false);
            log.error("Ошибка при обновлении пароля. ", e);
        }
        return objectNode;
    }
}
