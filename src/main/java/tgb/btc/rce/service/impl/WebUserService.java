package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.WebUser;
import tgb.btc.rce.repository.WebUserRepository;

@Service
public class WebUserService {

    private WebUserRepository webUserRepository;

    private BCryptPasswordEncoder passwordEncoder;

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
}
