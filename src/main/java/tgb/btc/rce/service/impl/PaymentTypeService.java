package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserRepository;

@Service
public class PaymentTypeService {

    private PaymentTypeRepository paymentTypeRepository;

    private DealRepository dealRepository;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    public Integer getTurnedCountByChatId(Long chatId) {
        Long dealPid = userRepository.getCurrentDealByChatId(chatId);
        Integer turnedCount = getTurnedCountByDeal(dealPid);
        if (turnedCount == 0) throw new BaseException("Не найдено ни одного включенного типа оплаты.");
        return turnedCount;
    }

    public Integer getTurnedCountByDeal(Long dealPid) {
        return paymentTypeRepository.countByDealTypeAndIsOnAndFiatCurrency(
                dealRepository.getDealTypeByPid(dealPid), true, dealRepository.getFiatCurrencyByPid(dealPid));
    }

    public PaymentType getFirstTurned(Long dealPid) {
        return paymentTypeRepository.getByDealTypeAndIsOnAndFiatCurrency(
                dealRepository.getDealTypeByPid(dealPid), true, dealRepository.getFiatCurrencyByPid(dealPid)).get(0);
    }
}
