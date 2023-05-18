package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.UserData;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;

@Repository
public interface UserDataRepository extends BaseRepository<UserData> {

    @Query("select longVariable from UserData where user.pid=:userPid")
    Long getLongByUserPid(Long userPid);

    @Query("select stringVariable from UserData where user.chatId=:chatId")
    String getStringByUserChatId(Long chatId);

    @Query("select dealTypeVariable from UserData where user.chatId=:chatId")
    DealType getDealTypeByChatId(Long chatId);

    @Query("select cryptoCurrency from UserData where user.chatId=:chatId")
    CryptoCurrency getCryptoCurrencyByChatId(Long chatId);

    @Query("select count(pid) from UserData where user.pid=:userPid")
    Long countByUserPid(Long userPid);

    @Modifying
    @Query("update UserData set longVariable=:longVariable where user.pid=:userPid")
    void updateLongByUserPid(Long userPid, Long longVariable);

    @Modifying
    @Query("update UserData set stringVariable=:stringVariable where user.pid in (select pid from User where chatId=:chatId)")
    void updateStringByUserChatId(Long chatId, String stringVariable);

    @Modifying
    @Query("update UserData set dealTypeVariable=:dealTypeVariable where user.pid in (select pid from User where chatId=:userChatId)")
    void updateDealTypeByUserChatId(Long userChatId, DealType dealTypeVariable);

    @Modifying
    @Query("delete from UserData where user.pid in (select pid from User where chatId=:userChatId)")
    void deleteByUserChatId(Long userChatId);

    @Modifying
    @Query("update UserData set cryptoCurrency=:cryptoCurrency where user.pid in (select pid from User where chatId=:userChatId)")
    void updateCryptoCurrencyByChatId(Long userChatId, CryptoCurrency cryptoCurrency);
}
