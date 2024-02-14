package tgb.btc.rce.service.processors.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.repository.web.ApiDealRepository;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.library.service.bean.bot.UserService;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class DealSupportService {

    private static final String DEAL_INFO = "Заявка на %s №%s\n" + "Дата,время: %s\n" + "Тип оплаты: %s\n" + "Кошелек: %s\n" + "Контакт: %s\n"
            + "Количество сделок: %s\n" + "ID: %s\n" + "Сумма %s: %s\n" + "Сумма: %s %s\n" + "Способ доставки: %s";

    private final DealService dealService;

    private final UserService userService;

    private ApiDealRepository apiDealRepository;

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
                + "Количество сделок: " + apiDealRepository.countByApiDealStatusAndApiUser_Pid(ApiDealStatus.ACCEPTED, apiDeal.getApiUser().getPid()) + "\n"
                + "Сумма " + apiDeal.getCryptoCurrency().getShortName() + ": " + apiDeal.getCryptoAmount() + "\n"
                + "Сумма " + apiDeal.getApiUser().getFiatCurrency().getDisplayName() + ": " + apiDeal.getAmount();
    }

    public String dealToString(Long pid) {
        Deal deal = dealService.getByPid(pid);
        User user = deal.getUser();
        String paymentTypeName = Objects.nonNull(deal.getPaymentType()) ? deal.getPaymentType().getName() : "Не установлен тип оплаты.";
        FiatCurrency fiatCurrency = deal.getFiatCurrency();
        return String.format(
                DEAL_INFO, deal.getDealType().getAccusative(), deal.getPid(),
                deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                paymentTypeName,
                deal.getWallet(),
                StringUtils.defaultIfEmpty(userService.getUsernameByChatId(user.getChatId()),
                        "Отсутствует"),
                dealService.getCountPassedByUserChatId(user.getChatId()), user.getChatId(),
                deal.getCryptoCurrency().getShortName(),
                deal.getCryptoAmount().setScale(8, RoundingMode.FLOOR).stripTrailingZeros()
                        .toPlainString(),
                deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
                Objects.nonNull(fiatCurrency) ? fiatCurrency.getGenitive() : "отсутствует",
                deal.getDeliveryType().getDisplayName()
        );
    }

    public ReplyKeyboard dealToStringButtons(Long pid) {
        return KeyboardUtil.buildInline(
                List.of(
                        InlineButton.builder()
                                .text("Подтвердить")
                                .data(Command.CONFIRM_USER_DEAL.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build(),
                        InlineButton.builder()
                                .text("Доп.верификация")
                                .data(Command.ADDITIONAL_VERIFICATION.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build(),
                        InlineButton.builder()
                                .text("Удалить")
                                .data(Command.DELETE_USER_DEAL.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build(),
                        InlineButton.builder()
                                .text("Удалить и заблокировать")
                                .data(Command.DELETE_DEAL_AND_BLOCK_USER.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + pid)
                                .build()
                )

        );
    }

}
