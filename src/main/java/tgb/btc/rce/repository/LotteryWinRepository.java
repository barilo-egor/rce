package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.LotteryWin;

@Repository
@Transactional
public interface LotteryWinRepository extends BaseRepository<LotteryWin> {
    @Query("select count(l) from LotteryWin l where l.user.pid in (select pid from User where chatId=:chatId)")
    Long getLotteryWinCount(Long chatId);

    @Modifying
    @Query("delete from LotteryWin where user.pid in (select pid from User where chatId=:chatId)")
    void deleteByUserChatId(@Param("chatId") Long chatId);
}
