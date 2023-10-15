package tgb.btc.rce.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.btc.rce.repository.WebUserRepository;
import tgb.btc.rce.web.util.SuccessResponseUtil;
import tgb.btc.rce.web.vo.SuccessResponse;

import java.security.Principal;

@RestController
@RequestMapping("/web/roles")
public class RoleController {

    private WebUserRepository webUserRepository;

    @Autowired
    public void setWebUserRepository(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @GetMapping("/getRole")
    private SuccessResponse<?> getRole(Principal principal) {
        return SuccessResponseUtil.data(webUserRepository.getByUsername(principal.getName()).getRoles());
    }

}
