package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.ReferralUser;

@Repository
@Transactional
public interface ReferralUserRepository  extends BaseRepository<ReferralUser> {
}
