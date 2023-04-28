package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.UserData;
import tgb.btc.rce.enums.DealType;

@Repository
public interface UserDataRepository extends BaseRepository<UserData> {

    @Query("select longVariable from UserData where user.pid=:userPid")
    Long getLongByUserPid(@Param("userPid") Long userPid);

    @Query("select stringVariable from UserData where user.pid=:userPid")
    String getStringByUserPid(@Param("userPid") Long userPid);

    @Query("select dealTypeVariable from UserData where user.pid=:userPid")
    DealType getDealTypeByUserPid(@Param("userPid") Long userPid);

    @Query("select count(pid) from UserData where user.pid=:userPid")
    Long countByUserPid(Long userPid);

    @Modifying
    @Query("update UserData set longVariable=:longVariable where user.pid=:userPid")
    void updateLongByUserPid(@Param("userPid") Long userPid, @Param("longVariable") Long longVariable);

    @Modifying
    @Query("update UserData set stringVariable=:stringVariable where user.pid=:userPid")
    void updateStringByUserPid(@Param("userPid") Long userPid, @Param("stringVariable") String stringVariable);

    @Modifying
    @Query("update UserData set dealTypeVariable=:dealTypeVariable where user.pid=:userPid")
    void updateDealTypeByUserPid(@Param("userPid") Long userPid, @Param("dealTypeVariable") DealType dealTypeVariable);
}
