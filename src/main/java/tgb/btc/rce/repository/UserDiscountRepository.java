package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.UserDiscount;

import java.math.BigDecimal;

@Repository
@Transactional
public interface UserDiscountRepository extends BaseRepository<UserDiscount> {

    /** SELECT **/
    @Query("select isRankDiscountOn from UserDiscount where user.chatId=:chatId")
    Boolean getRankDiscountByUserChatId(Long chatId);

    @Query("select personalBuy from UserDiscount where user.chatId=:chatId")
    BigDecimal getPersonalBuyByChatId(Long chatId);

    @Query("select personalSell from UserDiscount where user.chatId=:chatId")
    BigDecimal getPersonalSellByChatId(Long chatId);


    @Query("select count(pid) from UserDiscount where user.pid=:userPid")
    Long countByUserPid(Long userPid);


    /** UPDATE **/
    @Query("update UserDiscount set isRankDiscountOn=:isRankDiscountOn where user.pid=:pid")
    @Modifying
    void updateIsRankDiscountOnByPid(Boolean isRankDiscountOn, Long pid);

    @Query("update UserDiscount set personalBuy=:personalBuy where user.pid=:userPid")
    @Modifying
    void updatePersonalBuyByUserPid(BigDecimal personalBuy, Long userPid);

    @Query("update UserDiscount set personalSell=:personalSell where user.pid=:userPid")
    @Modifying
    void updatePersonalSellByUserPid(BigDecimal personalSell, Long userPid);

    @Modifying
    @Query("delete from UserDiscount where user.pid in (select pid from User where chatId=:userChatId)")
    void deleteByUserChatId(Long userChatId);
}
