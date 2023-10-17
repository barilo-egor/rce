package tgb.btc.rce.service.impl.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.UserDiscount;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.UserDiscountRepository;

@Service
public class UserDiscountService extends BasePersistService<UserDiscount> {

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public UserDiscountService(BaseRepository<UserDiscount> baseRepository) {
        super(baseRepository);
    }

    public boolean isExistByUserPid(Long userPid) {
        return userDiscountRepository.countByUserPid(userPid) > 0;
    }
}
