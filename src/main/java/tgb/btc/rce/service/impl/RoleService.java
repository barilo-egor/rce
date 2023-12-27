package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Role;
import tgb.btc.rce.enums.RoleConstants;
import tgb.btc.rce.repository.RoleRepository;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void initRoles() {
        List<String> rolesDBName = roleRepository.findAll().stream().map(Role::getName).collect(Collectors.toList());
        if (RoleConstants.values().length != rolesDBName.size()) {
            List<Role> roleList = Arrays.stream(RoleConstants.values()).filter(role -> !rolesDBName.contains(role.name())).map(role -> new Role(role.name())).collect(Collectors.toList());
            roleRepository.saveAll(roleList);
        }
    }
}
