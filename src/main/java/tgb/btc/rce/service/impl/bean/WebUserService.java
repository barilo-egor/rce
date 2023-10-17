package tgb.btc.rce.service.impl.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.WebUser;
import tgb.btc.rce.enums.RoleConstants;
import tgb.btc.rce.repository.RoleRepository;
import tgb.btc.rce.repository.WebUserRepository;

import java.util.Objects;

@Service
public class WebUserService implements UserDetailsService {

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

    public WebUser save(String username, String password, RoleConstants role) {
        WebUser webUser = new WebUser();
        webUser.setUsername(username);
        webUser.setPassword(passwordEncoder.encode(password));
        webUser.setEnabled(true);
        if (Objects.isNull(role)) {
            role = RoleConstants.ROLE_USER;
        }
        webUser.setRoles(roleRepository.getByName(role.name()));
        return webUserRepository.save(webUser);
    }

    public void changePassword(String userName, String newPassword) {
        WebUser webUser = getUser(userName);
        webUser.setPassword(passwordEncoder.encode(newPassword));
        webUserRepository.save(webUser);
    }

    public WebUser getUser(String username) {
        return (WebUser) loadUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return webUserRepository.getByUsername(username);
    }
}
