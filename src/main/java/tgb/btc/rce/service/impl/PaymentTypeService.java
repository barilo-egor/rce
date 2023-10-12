package tgb.btc.rce.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserRepository;

import java.util.List;

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

    public Integer getTurnedCountByDeal(Long chatId) {
        Long dealPid = userRepository.getCurrentDealByChatId(chatId);
        return paymentTypeRepository.countByDealTypeAndIsOnAndFiatCurrency(
                dealRepository.getDealTypeByPid(dealPid), true, dealRepository.getFiatCurrencyByPid(dealPid));
    }

    public PaymentType getFirstTurned(Long dealPid) {
        DealType dealType = dealRepository.getDealTypeByPid(dealPid);
        FiatCurrency fiatCurrency = dealRepository.getFiatCurrencyByPid(dealPid);
        List<PaymentType> paymentTypeList = paymentTypeRepository.getByDealTypeAndIsOnAndFiatCurrency(
                dealType, true, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypeList))
            throw new BaseException("Не найден ни один тип оплаты для " + dealType.name() + " " + fiatCurrency.name());
        return paymentTypeList.get(0);
    }

    public List<PaymentType> findAll() {
        return paymentTypeRepository.findAll();
    }
}
