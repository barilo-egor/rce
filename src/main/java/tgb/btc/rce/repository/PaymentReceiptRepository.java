package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.PaymentReceipt;

import java.util.List;

@Repository
public interface PaymentReceiptRepository extends BaseRepository<PaymentReceipt> {

    @Query("from PaymentReceipt where deal.pid in (select pid from Deal where user.chatId=:userChatId)")
    List<PaymentReceipt> getByDealsPids(Long userChatId);
}
