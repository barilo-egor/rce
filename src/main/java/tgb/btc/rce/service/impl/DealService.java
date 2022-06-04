package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Contact;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.PaymentType;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.DealRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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

    public Long getDealsCountByUserChatId(Long chatId) {
        return dealRepository.getDealsCountByUserChatId(chatId);
    }

    public Deal getByPid(Long pid) {
        return dealRepository.findByPid(pid);
    }

    public BigDecimal getAmountByPid(Long pid) {
        return dealRepository.getAmountByPid(pid);
    }

    public BigDecimal getRoundedAmountByPid(Long pid) {
        return dealRepository.getAmountByPid(pid).setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public void updateWalletByPid(String wallet, Long pid) {
        dealRepository.updateWalletByPid(wallet, pid);
    }

    public void updatePaymentTypeByPid(PaymentType paymentType, Long pid) {
        dealRepository.updatePaymentTypeByPid(paymentType, pid);
    }

    public void updateIsUsedPromoByPid(Boolean isUsedPromo, Long pid) {
        dealRepository.updateIsUsedPromoByPid(isUsedPromo, pid);
    }
}
