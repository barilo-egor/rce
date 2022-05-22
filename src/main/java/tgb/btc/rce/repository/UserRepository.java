package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;

import java.util.List;

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
    Integer getStepByChatId(@Param("chatId") Long chatId);

    @Query("select command from User where chatId=:chatId")
    Command getCommandByChatId(@Param("chatId") Long chatId);

    boolean existsByChatId(Long chatId);

    @Modifying
    @Query("update User set step=" + User.DEFAULT_STEP + ", command = 'START' where chatId=:chatId")
    void setDefaultValues(@Param("chatId") Long chatId);

    @Query("select isAdmin from User where chatId=:chatId")
    boolean isAdminByChatId(@Param("chatId") Long chatId);

    @Query("select referralBalance from User where chatId=:chatId")
    Integer getReferralBalanceByChatId(@Param("chatId") Long chatId);

    @Query("select referralUsers from User where chatId=:chatId")
    List<ReferralUser> getUserReferralsByChatId(@Param("chatId") Long chatId);

    User getByChatId(Long chatId);

    @Modifying
    @Query("update User set step=step + 1, command=:command where chatId=:chatId")
    void nextStep(@Param("chatId") Long chatId, @Param("command") Command command);

    @Query("select chatId from User where isAdmin=true")
    List<Long> getAdminsChatIds();

    @Modifying
    @Query("update User set bufferVariable=:bufferVariable where chatId=:chatId")
    void updateBufferVariable(@Param("chatId") Long chatId, @Param("bufferVariable") String bufferVariable);

    @Query("select bufferVariable from User where chatId=:chatId")
    String getBufferVariable(@Param("chatId") Long chatId);

    @Query("select chatId from User where isAdmin=false")
    List<Long> getChatIdsNotAdmins();

    @Modifying
    @Query("update User set isActive=:isActive where chatId=:chatId")
    void updateIsActiveByChatId(@Param("isActive") boolean isActive, @Param("chatId") Long chatId);
}
