package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.PaymentReceiptsRepository;

@Service
public class PaymentReceiptsService extends BasePersistService<PaymentReceipt> {

    private final PaymentReceiptsRepository paymentReceiptsRepository;

    @Autowired
    public PaymentReceiptsService(BaseRepository<PaymentReceipt> baseRepository, PaymentReceiptsRepository paymentReceiptsRepository) {
        super(baseRepository);
        this.paymentReceiptsRepository = paymentReceiptsRepository;
    }


}
