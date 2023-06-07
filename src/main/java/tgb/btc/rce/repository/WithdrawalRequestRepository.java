package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.WithdrawalRequest;

import java.util.List;

@Repository
@Transactional
public interface WithdrawalRequestRepository extends BaseRepository<WithdrawalRequest>{

    @Modifying
    @Query("update WithdrawalRequest set isActive=:isActive where pid=:pid")
    void updateIsActiveByPid(Boolean isActive, Long pid);

    @Query("select count(w) from WithdrawalRequest w where w.user.pid in (select pid from User where chatId=:chatId) and w.isActive=true")
    long getActiveByUserChatId(Long chatId);

    @Query("from WithdrawalRequest where isActive=true")
    List<WithdrawalRequest> getAllActive();

    @Query("select w.pid from WithdrawalRequest w where w.user.pid in (select pid from User where chatId=:chatId)")
    Long getPidByUserChatId(Long chatId);

    @Modifying
    @Query("delete from WithdrawalRequest where user.pid in (select pid from User where chatId=:userChatId)")
    void deleteByUserChatId(Long userChatId);
}
