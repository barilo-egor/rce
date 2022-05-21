package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.WithdrawalRequest;

@Repository
public interface WithdrawalRequestRepository extends BaseRepository<WithdrawalRequest>{
}
