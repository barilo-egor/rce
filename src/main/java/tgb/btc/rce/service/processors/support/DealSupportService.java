package tgb.btc.rce.service.processors.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.ApiDealStatus;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.enums.PaymentTypeEnum;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.repository.ApiUserRepository;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class DealSupportService {

    private final DealService dealService;

    private final UserService userService;

    private ApiDealRepository apiDealRepository;

    private ApiUserRepository apiUserRepository;

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    @Autowired
    public DealSupportService(DealService dealService, UserService userService) {
        this.dealService = dealService;
        this.userService = userService;
    }

    public String apiDealToString(Long pid) {
        ApiDeal apiDeal = apiDealRepository.getByPid(pid);
        return "API заявка на " + apiDeal.getDealType().getGenitive() + " №" + apiDeal.getPid() + "\n"
                + "Идентификатор клиента: " + apiDeal.getApiUser().getId() + "\n"
                + "Дата, время: " + apiDeal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n"
                + "Рекзвизиты клиента: " + apiDeal.getRequisite() + "\n"
                + "Реквизиты оплаты: " + apiDeal.getApiUser().getRequisite(apiDeal.getDealType()) + "\n"
                + "Количество сделок: " + apiDealRepository.getCountByApiDealStatusAndApiUserPid(ApiDealStatus.ACCEPTED, apiDeal.getApiUser().getPid()) + "\n"
                + "Сумма " + apiDeal.getCryptoCurrency().getShortName() + ": " + apiDeal.getCryptoAmount() + "\n"
                + "Сумма " + apiDeal.getApiUser().getFiatCurrency().getGenitive() + ": " + apiDeal.getAmount();
    }

    public String dealToString(Long pid) {
        Deal deal = dealService.getByPid(pid);
        User user = deal.getUser();
        // getPaymentTypeEnum используется для старых сделок
        PaymentTypeEnum paymentTypeEnum = deal.getPaymentTypeEnum();
        String paymentTypeName = Objects.nonNull(paymentTypeEnum)
                                 ? paymentTypeEnum.getDisplayName()
                                 : Objects.nonNull(deal.getPaymentType()) ? deal.getPaymentType().getName() : "Не установлен тип оплаты.";
        FiatCurrency fiatCurrency = deal.getFiatCurrency();
        return String.format(
                BotStringConstants.DEAL_INFO, deal.getDealType().getAccusative(), deal.getPid(),
                deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                paymentTypeName,
                deal.getWallet(),
                StringUtils.defaultIfEmpty(userService.getUsernameByChatId(user.getChatId()),
                                           BotStringConstants.ABSENT),
                dealService.getCountPassedByUserChatId(user.getChatId()), user.getChatId(),
                deal.getCryptoCurrency().getShortName(),
                deal.getCryptoAmount().setScale(8, RoundingMode.FLOOR).stripTrailingZeros()
                        .toPlainString(),
                deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
                Objects.nonNull(fiatCurrency) ? fiatCurrency.getDisplayName() : "отсутствует"
        );
    }

    public ReplyKeyboard dealToStringButtons(Long pid) {
        return KeyboardUtil.buildInline(
                List.of(
                        InlineButton.builder()
                                .text("Подтвердить")
                                .data(Command.CONFIRM_USER_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build(),
                        InlineButton.builder()
                                .text("Доп.верификация")
                                .data(Command.ADDITIONAL_VERIFICATION.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build(),
                        InlineButton.builder()
                                .text("Удалить")
                                .data(Command.DELETE_USER_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build(),
                        InlineButton.builder()
                                .text("Удалить и заблокировать")
                                .data(Command.DELETE_DEAL_AND_BLOCK_USER.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build()
                )

        );
    }

}
