package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.UserDiscount;

import java.math.BigDecimal;

@Repository
public interface UserDiscountRepository extends BaseRepository<UserDiscount> {

    /** SELECT **/
    @Query("select isRankDiscountOn from UserDiscount where user.chatId=:chatId")
    Boolean getRankDiscountByUserChatId(@Param("chatId") Long chatId);

    @Query("select personalBuy from UserDiscount where user.chatId=:chatId")
    BigDecimal getPersonalBuyByChatId(@Param("chatId") Long chatId);

    /** UPDATE **/
    @Query("update UserDiscount set isRankDiscountOn=:isRankDiscountOn where user.chatId=:chatId")
    @Modifying
    void updateIsRankDiscountOnByChatId(@Param("isRankDiscountOn") Boolean isRankDiscountOn, @Param("chatId") Long chatId);
}
