package tgb.btc.rce.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.btc.library.repository.web.WebUserRepository;
import tgb.btc.web.constant.ControllerMapping;
import tgb.btc.web.util.SuccessResponseUtil;
import tgb.btc.web.vo.SuccessResponse;

import java.security.Principal;

@RestController
@RequestMapping(ControllerMapping.ROLES)
public class RoleController {

    private WebUserRepository webUserRepository;

    @Autowired
    public void setWebUserRepository(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @GetMapping("/get")
    private SuccessResponse<?> getRole(Principal principal) {
        return SuccessResponseUtil.data(webUserRepository.getByUsername(principal.getName()).getRoles());
    }

}
