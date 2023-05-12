package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.LotteryWin;

@Repository
@Transactional
public interface LotteryWinRepository extends BaseRepository<LotteryWin> {
    @Query("select count(l) from LotteryWin l where l.user.chatId=:chatId")
    Long getLotteryWinCount(@Param("chatId") Long chatId);
}
