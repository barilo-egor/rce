package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.UserDiscount;

import java.math.BigDecimal;

@Repository
@Transactional
public interface UserDiscountRepository extends BaseRepository<UserDiscount> {

    /** SELECT **/
    @Query("select isRankDiscountOn from UserDiscount where user.chatId=:chatId")
    Boolean getRankDiscountByUserChatId(@Param("chatId") Long chatId);

    @Query("select personalBuy from UserDiscount where user.chatId=:chatId")
    BigDecimal getPersonalBuyByChatId(@Param("chatId") Long chatId);

    @Query("select personalSell from UserDiscount where user.chatId=:chatId")
    BigDecimal getPersonalSellByChatId(@Param("chatId") Long chatId);


    @Query("select count(pid) from UserDiscount where user.pid=:userPid")
    Long countByUserPid(@Param("userPid") Long userPid);


    /** UPDATE **/
    @Query("update UserDiscount set isRankDiscountOn=:isRankDiscountOn where user.chatId=:chatId")
    @Modifying
    void updateIsRankDiscountOnByChatId(@Param("isRankDiscountOn") Boolean isRankDiscountOn, @Param("chatId") Long chatId);

    @Query("update UserDiscount set personalBuy=:personalBuy where user.pid=:userPid")
    @Modifying
    void updatePersonalBuyByUserPid(@Param("personalBuy") BigDecimal personalBuy, @Param("userPid") Long userPid);

    @Query("update UserDiscount set personalSell=:personalSell where user.pid=:userPid")
    @Modifying
    void updatePersonalSellByUserPid(@Param("personalSell") BigDecimal personalSell, @Param("userPid") Long userPid);
}
