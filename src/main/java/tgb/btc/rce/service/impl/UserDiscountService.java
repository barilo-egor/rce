package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.service.IUserDiscountService;

@Service
public class UserDiscountService implements IUserDiscountService {
    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Override
    public boolean isExistByUserPid(Long userPid) {
        return userDiscountRepository.countByUserPid(userPid) > 0;
    }

}
