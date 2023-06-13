package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.util.DealPromoUtil;
import tgb.btc.rce.vo.ReportDealVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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

    public Long getDealsCountByUserChatId(Long chatId) {
        return dealRepository.getPassedDealsCountByUserChatId(chatId);
    }

    public Deal getByPid(Long pid) {
        return dealRepository.findByPid(pid);
    }

    public void updatePaymentTypeByPid(PaymentType paymentType, Long pid) {
        dealRepository.updatePaymentTypeByPid(paymentType, pid);
    }

    public Long getPidActiveDealByChatId(Long chatId) {
        return dealRepository.getPidActiveDealByChatId(chatId);
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
        deal.setDateTime(LocalDateTime.now());
        deal.setDate(LocalDate.now());
        deal.setDealType(dealType);
        deal.setUser(userRepository.findByChatId(chatId));
        Deal savedDeal = save(deal);
        userRepository.updateCurrentDealByChatId(savedDeal.getPid(), chatId);
        return savedDeal;
    }

    public boolean isFirstDeal(Long chatId) {
        return getDealsCountByUserChatId(chatId) < 1;
    }

    public boolean isAvailableForPromo(Long chatId) {
        return !DealPromoUtil.isNone() && isFirstDeal(chatId);
    }

    public List<ReportDealVO> findAllForUsersReport() {
        List<Object[]> raws = dealRepository.findAllForUsersReport();
        List<ReportDealVO> deals = new ArrayList<>();
        log.info("Маппинг сделок.");
        for (Object[] raw : raws) {
            deals.add(ReportDealVO.builder()
                    .pid((Long) raw[0])
                    .userPid((Long) raw[1])
                    .dealType((DealType) raw[2])
                    .cryptoCurrency((CryptoCurrency) raw[3])
                    .cryptoAmount((BigDecimal) raw[4])
                    .fiatCurrency((FiatCurrency) raw[5])
                    .amount((BigDecimal) raw[6])
                    .build());
        }
        return deals;
    }
}
