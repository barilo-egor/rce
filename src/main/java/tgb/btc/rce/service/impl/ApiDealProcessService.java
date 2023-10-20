package tgb.btc.rce.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.bean.web.api.ApiUser;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.repository.web.ApiDealRepository;
import tgb.btc.library.repository.web.ApiUserRepository;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.library.util.web.JacksonUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.vo.calculate.DealAmount;
import tgb.btc.rce.vo.web.CalculateDataForm;
import tgb.btc.web.constant.enums.ApiStatusCode;
import tgb.btc.web.util.ApiResponseUtil;
import tgb.btc.web.vo.form.ApiDealVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ApiDealProcessService {

    private ApiUserRepository apiUserRepository;

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
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    public ObjectNode newDeal(String token, DealType dealType, BigDecimal amount, BigDecimal cryptoAmount,
                              CryptoCurrency cryptoCurrency, String requisite, FiatCurrency fiatCurrency) {
        ApiDealVO apiDealVO = new ApiDealVO(token, dealType, amount, cryptoAmount, cryptoCurrency, requisite, fiatCurrency);
        ApiStatusCode code = apiDealVO.verify();
        if (Objects.nonNull(code)) return ApiResponseUtil.build(code);

        if (apiUserRepository.countByToken(token) == 0) ApiResponseUtil.build(ApiStatusCode.USER_NOT_FOUND);

        ApiUser apiUser = apiUserRepository.getByToken(token);
        CalculateDataForm.CalculateDataFormBuilder builder = CalculateDataForm.builder();
        builder.dealType(apiDealVO.getDealType())
                .fiatCurrency(Objects.nonNull(apiDealVO.getFiatCurrency())
                        ? apiDealVO.getFiatCurrency()
                        : apiUser.getFiatCurrency())
                .usdCourse(apiUser.getCourse(apiUser.getFiatCurrency()).getCourse())
                .cryptoCourse(cryptoCurrencyService.getCurrency(apiDealVO.getCryptoCurrency()))
                .personalDiscount(apiUser.getPersonalDiscount())
                .cryptoCurrency(apiDealVO.getCryptoCurrency());
        if (Objects.nonNull(apiDealVO.getAmount())) builder.amount(apiDealVO.getAmount());
        else builder.cryptoAmount(apiDealVO.getCryptoAmount());
        ApiDeal apiDeal = create(apiDealVO, apiUser, calculateService.calculate(builder.build()));
        BigDecimal minSum = BotVariablePropertiesUtil.getBigDecimal(VariableType.MIN_SUM, dealType, cryptoCurrency);
        if (apiDeal.getCryptoAmount().compareTo(minSum) < 0)
            return ApiResponseUtil.build(ApiStatusCode.MIN_SUM,
                    JacksonUtil.getEmpty().put("minSum", BigDecimalUtil.roundToPlainString(minSum, 8)));

        return ApiResponseUtil.build(ApiStatusCode.CREATED_DEAL,
                dealData(apiDeal, apiUser.getRequisite(apiDeal.getDealType())));
    }

    public ApiDeal create(ApiDealVO apiDealVO, ApiUser apiUser, DealAmount dealAmount) {
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
        if (DealType.isBuy(apiDeal.getDealType())) data.put("cryptoAmount", BigDecimalUtil.roundToPlainString(apiDeal.getCryptoAmount(), 8));
        else data.put("amount", apiDeal.getAmount());
        return data;
    }
}
