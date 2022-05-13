package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.WithdrawalRequest;
import tgb.btc.rce.repository.BaseRepository;

@Service
public class WithdrawalRequestService extends BasePersistService<WithdrawalRequest> {

    @Autowired
    public WithdrawalRequestService(BaseRepository<WithdrawalRequest> baseRepository) {
        super(baseRepository);
    }
}
