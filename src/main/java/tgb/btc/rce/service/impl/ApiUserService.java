package tgb.btc.rce.service.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.ApiUser;
import tgb.btc.rce.repository.ApiUserRepository;

import java.time.LocalDate;

@Service
public class ApiUserService {

    private ApiUserRepository apiUserRepository;

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    public ApiUser register(ApiUser apiUser) {
        apiUser.setRegistrationDate(LocalDate.now());
        String token = RandomStringUtils.randomAlphanumeric(40);
        while (apiUserRepository.countByToken(token) > 0) {
            token = RandomStringUtils.randomAlphanumeric(40);
        }
        apiUser.setToken(token);
        apiUser.setIsBanned(false);
        return apiUserRepository.save(apiUser);
    }

    public boolean isExistsById(String id) {
        return apiUserRepository.countById(id) > 0;
    }
}
