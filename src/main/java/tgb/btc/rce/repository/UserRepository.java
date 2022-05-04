package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;

@Repository
@Transactional
public interface UserRepository extends BaseRepository<User> {
    User findByChatId(Long chatId);

    @Query("select pid from User where chatId=:chatId")
    Long getPidByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Query("update User set step=:step where chatId=:chatId")
    void updateStepByChatId(@Param("step") int step,@Param("chatId") Long chatId);

    @Query("select step from User where chatId=:chatId")
    int getStepByChatId(@Param("chatId") Long chatId);

    @Query("select command from User where chatId=:chatId")
    Command getCommandByChatId(@Param("chatId") Long chatId);
}
