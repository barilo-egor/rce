package tgb.btc.rce.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.bean.ApiUser;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.repository.ApiUserRepository;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.vo.calculate.DealAmount;
import tgb.btc.rce.vo.web.CalculateDataForm;
import tgb.btc.rce.web.controller.api.enums.StatusCode;
import tgb.btc.rce.web.util.ApiResponseUtil;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.ApiDealVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ApiDealService {

    private ApiDealRepository apiDealRepository;

    private CryptoCurrencyService cryptoCurrencyService;

    private CalculateService calculateService;

    private ApiUserRepository apiUserRepository;

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

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    public ObjectNode newDeal(String token, DealType dealType, BigDecimal amount, BigDecimal cryptoAmount,
                              CryptoCurrency cryptoCurrency, String requisite, FiatCurrency fiatCurrency) {
        ApiDealVO apiDealVO = new ApiDealVO(token, dealType, amount, cryptoAmount, cryptoCurrency, requisite, fiatCurrency);
        StatusCode code = apiDealVO.verify();
        if (Objects.nonNull(code)) return ApiResponseUtil.build(code);

        if (apiUserRepository.countByToken(token) == 0) ApiResponseUtil.build(StatusCode.USER_NOT_FOUND);

        ApiUser apiUser = apiUserRepository.getByToken(token);
        ApiDeal apiDeal = create(apiDealVO, apiUser);
        BigDecimal minSum = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.MIN_SUM, dealType, cryptoCurrency);
        if (apiDeal.getCryptoAmount().compareTo(minSum) < 0)
            return ApiResponseUtil.build(StatusCode.MIN_SUM,
                    JacksonUtil.getEmpty().put("minSum", BigDecimalUtil.roundToPlainString(minSum, 8)));

        return ApiResponseUtil.build(StatusCode.CREATED_DEAL,
                dealData(apiDeal, apiUser.getRequisite(apiDeal.getDealType())));
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

    public ObjectNode dealData(ApiDeal apiDeal, String requisite) {
        ObjectNode data = JacksonUtil.toObjectNode(apiDeal, deal -> JacksonUtil.getEmpty()
                .put("id", deal.getPid())
                .put("amountToPay", BigDecimalUtil.roundToPlainString(deal.getAmountToPay(), 8))
                .put("requisite", requisite));
        if (DealType.isBuy(apiDeal.getDealType())) data.put("cryptoAmount", apiDeal.getCryptoAmount());
        else data.put("amount", apiDeal.getAmount());
        return data;
    }
}
