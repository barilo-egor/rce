package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.LotteryWin;

@Repository
@Transactional
public interface LotteryWinRepository extends BaseRepository<LotteryWin> {
}
