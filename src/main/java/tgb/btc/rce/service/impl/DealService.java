package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Contact;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.DealRepository;

import java.math.BigDecimal;

@Service
public class DealService extends BasePersistService<Deal> {

    private final DealRepository dealRepository;

    @Autowired
    public DealService(BaseRepository<Deal> baseRepository, DealRepository dealRepository) {
        super(baseRepository);
        this.dealRepository = dealRepository;
    }

    public void updateCryptoCurrencyByPid(Long pid, CryptoCurrency currency) {
        dealRepository.updateCryptoCurrencyByPid(pid, currency);
    }

    public CryptoCurrency getCryptoCurrencyByPid(@Param("pid") Long pid) {
        return dealRepository.getCryptoCurrencyByPid(pid);
    }

    public boolean existByPid(Long pid) {
        return dealRepository.existsById(pid);
    }

    public void updateCryptoAmountByPid(BigDecimal cryptoAmount, Long pid) {
        dealRepository.updateCryptoAmountByPid(cryptoAmount, pid);
    }

    public void updateAmountByPid(BigDecimal amount, Long pid) {
        dealRepository.updateAmountByPid(amount, pid);
    }
}