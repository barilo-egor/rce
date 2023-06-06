package tgb.btc.lib.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.lib.bean.ReferralUser;

@Repository
@Transactional
public interface ReferralUserRepository  extends BaseRepository<ReferralUser> {
}
