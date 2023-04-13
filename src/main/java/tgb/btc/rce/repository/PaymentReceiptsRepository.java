package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.PaymentReceipt;

@Repository
@Transactional
public interface PaymentReceiptsRepository extends BaseRepository<PaymentReceipt> {
}
