package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.WithdrawalRequest;

import java.util.List;

@Repository
@Transactional
public interface WithdrawalRequestRepository extends BaseRepository<WithdrawalRequest>{

    @Modifying
    @Query("update WithdrawalRequest set isActive=:isActive where pid=:pid")
    void updateIsActiveByPid(@Param("isActive") Boolean isActive, @Param("pid") Long pid);

    @Query("from WithdrawalRequest where isActive=true")
    List<WithdrawalRequest> getAllActive();
}
