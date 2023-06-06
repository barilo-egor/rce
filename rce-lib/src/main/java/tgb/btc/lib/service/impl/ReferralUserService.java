package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.lib.bean.ReferralUser;
import tgb.btc.lib.repository.BaseRepository;
import tgb.btc.lib.repository.ReferralUserRepository;

@Service
public class ReferralUserService extends BasePersistService<ReferralUser> {
    private final ReferralUserRepository referralUserRepository;

    @Autowired
    public ReferralUserService(BaseRepository<ReferralUser> baseRepository,
                               ReferralUserRepository referralUserRepository) {
        super(baseRepository);
        this.referralUserRepository = referralUserRepository;
    }

    public ReferralUser save(ReferralUser referralUser) {
        return referralUserRepository.save(referralUser);
    }
}
