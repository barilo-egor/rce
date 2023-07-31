package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.bean.ApiUser;
import tgb.btc.rce.enums.ApiDealStatus;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.vo.calculate.DealAmount;
import tgb.btc.rce.vo.web.CalculateDataForm;
import tgb.btc.rce.web.vo.ApiDealVO;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ApiDealService {

    private ApiDealRepository apiDealRepository;

    private CryptoCurrencyService cryptoCurrencyService;

    private CalculateService calculateService;

    @Autowired
    public void setCryptoCurrencyService(CryptoCurrencyService cryptoCurrencyService) {
        this.cryptoCurrencyService = cryptoCurrencyService;
    }

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    public ApiDeal create(ApiDealVO apiDealVO, ApiUser apiUser) {
        CalculateDataForm.CalculateDataFormBuilder builder = CalculateDataForm.builder();
        builder.dealType(apiDealVO.getDealType())
                .fiatCurrency(Objects.nonNull(apiDealVO.getFiatCurrency())
                        ? apiDealVO.getFiatCurrency()
                        : apiUser.getFiatCurrency())
                .usdCourse(apiUser.getUsdCourse())
                .cryptoCourse(cryptoCurrencyService.getCurrency(apiDealVO.getCryptoCurrency()))
                .personalDiscount(apiUser.getPersonalDiscount())
                .cryptoCurrency(apiDealVO.getCryptoCurrency());
        if (Objects.nonNull(apiDealVO.getAmount())) builder.amount(apiDealVO.getAmount());
        else builder.cryptoAmount(apiDealVO.getCryptoAmount());
        DealAmount dealAmount = calculateService.calculate(builder.build());

        ApiDeal apiDeal = new ApiDeal();
        apiDeal.setApiUser(apiUser);
        apiDeal.setDateTime(LocalDateTime.now());
        apiDeal.setDealType(apiDealVO.getDealType());
        apiDeal.setAmount(dealAmount.getAmount());
        apiDeal.setCryptoAmount(dealAmount.getCryptoAmount());
        apiDeal.setApiDealStatus(ApiDealStatus.CREATED);
        apiDeal.setCryptoCurrency(apiDealVO.getCryptoCurrency());
        apiDeal.setRequisite(apiDealVO.getRequisite());
        return apiDealRepository.save(apiDeal);
    }
}
