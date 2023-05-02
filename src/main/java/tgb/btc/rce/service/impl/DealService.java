package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PaymentTypeEnum;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DealService extends BasePersistService<Deal> {

    private final DealRepository dealRepository;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    public void updateDiscountByPid(BigDecimal discount, Long pid) {
        dealRepository.updateDiscountByPid(discount, pid);
    }

    public void updateCommissionByPid(BigDecimal commission, Long pid) {
        dealRepository.updateCommissionByPid(commission, pid);
    }

    public BigDecimal getCommissionByPid(Long pid) {
        return dealRepository.getCommissionByPid(pid);
    }

    public void updateUsedReferralDiscountByPid(Boolean isUsedReferralDiscount, Long pid) {
        dealRepository.updateUsedReferralDiscountByPid(isUsedReferralDiscount, pid);
    }

    public Long getDealsCountByUserChatId(Long chatId) {
        return dealRepository.getDealsCountByUserChatId(chatId);
    }

    public Long getNotCurrentDealsCountByUserChatId(Long chatId, DealType dealType) {
        return dealRepository.getNotCurrentDealsCountByUserChatId(chatId, dealType);
    }

    public Deal getByPid(Long pid) {
        return dealRepository.findByPid(pid);
    }

    public BigDecimal getAmountByPid(Long pid) {
        return dealRepository.getAmountByPid(pid);
    }

    public BigDecimal getDiscountByPid(Long pid) {
        return dealRepository.getDiscountByPid(pid);
    }

    public BigDecimal getRoundedAmountByPid(Long pid) {
        return dealRepository.getAmountByPid(pid).setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public void updateWalletByPid(String wallet, Long pid) {
        dealRepository.updateWalletByPid(wallet, pid);
    }

    public void updatePaymentTypeEnumByPid(PaymentTypeEnum paymentTypeEnum, Long pid) {
        dealRepository.updatePaymentTypeEnumByPid(paymentTypeEnum, pid);
    }

    public void updatePaymentTypeByPid(PaymentType paymentType, Long pid) {
        dealRepository.updatePaymentTypeByPid(paymentType, pid);
    }


    public void updateIsUsedPromoByPid(Boolean isUsedPromo, Long pid) {
        dealRepository.updateIsUsedPromoByPid(isUsedPromo, pid);
    }

    public void updateIsActivePromoByPid(Boolean isActive, Long pid) {
        dealRepository.updateIsUsedPromoByPid(isActive, pid);
    }

    public Long getActiveDealsCountByUserChatId(Long chatId) {
        return dealRepository.getActiveDealsCountByUserChatId(chatId);
    }

    public Long getPidActiveDealByChatId(Long chatId) {
        return dealRepository.getPidActiveDealByChatId(chatId);
    }

    public void updateIsActiveByPid(Boolean isActive, Long pid) {
        dealRepository.updateIsActiveByPid(isActive, pid);
    }

    public void updateIsCurrentByPid(Boolean isCurrent, Long pid) {
        dealRepository.updateIsCurrentByPid(isCurrent, pid);
    }

    public Long getCountPassedByUserChatId(Long chatId) {
        return dealRepository.getCountPassedByUserChatId(chatId);
    }

    public List<Long> getActiveDealPids() {
        return dealRepository.getActiveDealPids();
    }

    public Long getUserChatIdByDealPid(Long pid) {
        return dealRepository.getUserChatIdByDealPid(pid);
    }

    public List<Deal> getByDateBetween(LocalDate startDate, LocalDate endDate) {
        return dealRepository.getByDateBetween(startDate, endDate);
    }

    public List<Deal> getByDate(LocalDate dateTime) {
        return dealRepository.getPassedByDate(dateTime);
    }

    public String getWalletFromLastNotCurrentByChatId(Long chatId, DealType dealType) {
        return dealRepository.getWalletFromLastNotCurrentByChatId(chatId, dealType);
    }

    public DealType getDealTypeByPid(Long pid) {
        return dealRepository.getDealTypeByPid(pid);
    }

    @Transactional(readOnly = true)
    public List<PaymentReceipt> getPaymentReceipts(Long dealPid) {
        Deal deal = getByPid(dealPid);
        return new ArrayList<>(deal.getPaymentReceipts());
    }

    public Deal createNewDeal(DealType dealType, Long chatId) {
        Deal deal = new Deal();
        deal.setActive(false);
        deal.setPassed(false);
        deal.setCurrent(true);
        deal.setDateTime(LocalDateTime.now());
        deal.setDate(LocalDate.now());
        deal.setDealType(dealType);
        deal.setUser(userRepository.findByChatId(chatId));
        Deal savedDeal = save(deal);
        userRepository.updateCurrentDealByChatId(savedDeal.getPid(), chatId);
        return savedDeal;
    }
}
