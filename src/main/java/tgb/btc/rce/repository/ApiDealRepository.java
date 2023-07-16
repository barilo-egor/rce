package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.enums.ApiDealStatus;

@Repository
public interface ApiDealRepository extends BaseRepository<ApiDeal> {

    long countByPid(Long pid);

    @Query("select apiDealStatus from ApiDeal where pid=:pid")
    ApiDealStatus getApiDealStatusByPid(Long pid);

    @Modifying
    @Query("update ApiDeal set apiDealStatus=:status where pid=:pid")
    void updateApiDealStatusByPid(ApiDealStatus status, Long pid);

    @Query("select count(pid) from ApiDeal where apiDealStatus=:status and apiUser.pid=:userPid")
    long getCountByApiDealStatusAndApiUserPid(ApiDealStatus status, Long userPid);
}
