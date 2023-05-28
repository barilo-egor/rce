package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface UserRepository extends BaseRepository<User> {

    User findByChatId(Long chatId);

    @Query("select pid from User where chatId=:chatId")
    Long getPidByChatId(Long chatId);

    @Query("select step from User where chatId=:chatId")
    Integer getStepByChatId(Long chatId);

    @Query("select command from User where chatId=:chatId")
    Command getCommandByChatId(Long chatId);

    boolean existsByChatId(Long chatId);

    @Modifying
    @Query("update User set step=" + User.DEFAULT_STEP + ", command = 'START' where chatId=:chatId")
    void setDefaultValues(Long chatId);

    @Query("select isAdmin from User where chatId=:chatId")
    boolean isAdminByChatId(Long chatId);

    @Query("select referralBalance from User where chatId=:chatId")
    Integer getReferralBalanceByChatId(Long chatId);

    @Query("select u.referralUsers from User u where u.chatId=:chatId")
    List<ReferralUser> getUserReferralsByChatId(Long chatId);

    User getByChatId(Long chatId);

    @Modifying
    @Query("update User set step=step + 1, command=:command where chatId=:chatId")
    void nextStep(Long chatId, Command command);

    @Modifying
    @Query("update User set step=step + 1 where chatId=:chatId")
    void nextStep(Long chatId);

    @Modifying
    @Query("update User set step=step - 1 where chatId=:chatId")
    void previousStep(Long chatId);

    @Query("select chatId from User where isAdmin=true")
    List<Long> getAdminsChatIds();

    @Modifying
    @Query("update User set bufferVariable=:bufferVariable where chatId=:chatId")
    void updateBufferVariable(Long chatId, String bufferVariable);

    @Query("select bufferVariable from User where chatId=:chatId")
    String getBufferVariable(Long chatId);

    @Query("select chatId from User where isAdmin=false and isActive=true and isBanned=false")
    List<Long> getChatIdsNotAdminsAndIsActiveAndNotBanned();

    @Modifying
    @Query("update User set isActive=:isActive where chatId=:chatId")
    void updateIsActiveByChatId(boolean isActive, @Param("chatId") Long chatId);

    @Query("select isBanned from User where chatId=:chatId")
    Boolean getIsBannedByChatId(Long chatId);

    /**
     * use userService.ban() and userService.unban()
     */
    @Modifying
    @Query("update User set isBanned=:isBanned where chatId=:chatId")
    void updateIsBannedByChatId(Long chatId, Boolean isBanned);

    @Modifying
    @Query("update User set currentDeal=:dealPid where chatId=:chatId")
    void updateCurrentDealByChatId(Long dealPid, @Param("chatId") Long chatId);

    @Query("select currentDeal from User where chatId=:chatId")
    Long getCurrentDealByChatId(Long chatId);

    @Query("select username from User where chatId=:chatId")
    String getUsernameByChatId(Long chatId);

    @Modifying
    @Query("update User set command=:command where chatId=:chatId")
    void updateCommandByChatId(Command command, @Param("chatId") Long chatId);

    @Modifying
    @Query("update User set referralBalance=:referralBalance where chatId=:chatId")
    void updateReferralBalanceByChatId(Integer referralBalance, @Param("chatId") Long chatId);

    @Modifying
    @Query("update User set charges=:charges where chatId=:chatId")
    void updateChargesByChatId(Integer charges, @Param("chatId") Long chatId);

    @Query("select charges from User where chatId=:chatId")
    Integer getChargesByChatId(Long chatId);

    @Query("update User set referralPercent=:referralPercent where chatId=:chatId")
    @Modifying
    void updateReferralPercent(BigDecimal referralPercent, Long chatId);

    @Query("select referralPercent from User where chatId=:chatId")
    BigDecimal getReferralPercentByChatId(Long chatId);

    @Query("select pid from User ")
    List<Long> getPids();

    @Modifying
    @Query("update User set step=:step, command=:command where chatId=:chatId")
    void updateStepAndCommandByChatId(Long chatId, Command command, Integer step);

    @Modifying
    @Query("update User set step=:stepwhere where chatId=:chatId")
    void updateStepByChatId(Long chatId, Integer step);

    /**
     * Reports
     */

    @Query("select count(pid) from User where registrationDate between :localDateTime1 and :localDateTime2")
    Integer countByRegistrationDate(LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    @Query("select chatId from User where registrationDate between :localDateTime1 and :localDateTime2 and fromChatId is not null")
    List<Long> getChatIdsByRegistrationDateAndFromChatIdNotNull(LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    @Query("select chatId from User where pid=:pid")
    Long getChatIdByPid(Long pid);

    @Modifying
    @Query("update User set isAdmin=:isAdmin where chatId=:chatId")
    void updateIsAdminByChatId(Long chatId, Boolean isAdmin);

}
