package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.SpamBan;

import java.util.List;

@Repository
public interface SpamBanRepository extends BaseRepository<SpamBan> {

    @Query("select user.pid from SpamBan where pid=:pid")
    Long getUserPidByPid(Long pid);

    @Query("select user.chatId from SpamBan where pid=:pid")
    Long getUserChatIdByPid(Long pid);

    @Query("select pid from SpamBan")
    List<Long> getPids();

    @Modifying
    @Query("delete from SpamBan where user.pid=:userPid")
    void deleteByUserPid(Long userPid);

    long countByPid(Long pid);
}
