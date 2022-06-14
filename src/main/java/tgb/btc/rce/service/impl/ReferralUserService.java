package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.ReferralUserRepository;

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
