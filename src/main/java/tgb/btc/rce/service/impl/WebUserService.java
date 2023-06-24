package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.WebUser;
import tgb.btc.rce.enums.RoleConstants;
import tgb.btc.rce.repository.RoleRepository;
import tgb.btc.rce.repository.WebUserRepository;
import tgb.btc.rce.vo.web.RegistrationVO;

import java.util.Set;

@Service
public class WebUserService {

    private WebUserRepository webUserRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setWebUserRepository(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @Autowired
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public WebUser save(WebUser webUser) {
        webUser.setPassword(passwordEncoder.encode(webUser.getPassword()));
        return webUserRepository.save(webUser);
    }

    public WebUser save(RegistrationVO registrationVO) {
        WebUser webUser = new WebUser();
        webUser.setUsername(registrationVO.getUsername());
        webUser.setPassword(passwordEncoder.encode(registrationVO.getPassword()));
        webUser.setEnabled(true);
        webUser.setRoles(Set.of(roleRepository.getByName(RoleConstants.USER.name())));
        return webUserRepository.save(webUser);
    }
}
