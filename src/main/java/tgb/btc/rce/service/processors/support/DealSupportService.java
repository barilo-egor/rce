package tgb.btc.rce.service.processors.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.enums.IDeliveryTypeService;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.vo.InlineButton;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DealSupportService {


    private IReadUserService readUserService;

    private IApiDealService apiDealService;

    private IReadDealService readDealService;

    private IDealCountService dealCountService;

    private IGroupChatService groupChatService;

    private IKeyboardBuildService keyboardBuildService;

    private ICallbackQueryService callbackQueryService;

    private IDeliveryTypeService deliveryTypeService;

    private IBigDecimalService bigDecimalService;

    private ICommandService commandService;

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public void setBigDecimalService(IBigDecimalService bigDecimalService) {
        this.bigDecimalService = bigDecimalService;
    }

    @Autowired
    public void setDeliveryTypeService(IDeliveryTypeService deliveryTypeService) {
        this.deliveryTypeService = deliveryTypeService;
    }

    @Autowired
    public void setCallbackQueryService(ICallbackQueryService callbackQueryService) {
        this.callbackQueryService = callbackQueryService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setGroupChatService(IGroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @Autowired
    public void setDealCountService(IDealCountService dealCountService) {
        this.dealCountService = dealCountService;
    }

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    public String apiDealToRequestString(Long pid) {
        ApiDeal apiDeal = apiDealService.findById(pid);
        String dealString = apiDealToString(pid);
        if (CryptoCurrency.BITCOIN.equals(apiDeal.getCryptoCurrency()) && DealType.BUY.equals(apiDeal.getDealType()))
            return dealString + "\nСтрока для вывода:\n<code>" + apiDeal.getRequisite() + "," + bigDecimalService.toPlainString(apiDeal.getCryptoAmount()) + "</code>";
        else
            return dealString;
    }

    public String apiDealToString(Long pid) {
        ApiDeal apiDeal = apiDealService.getByPid(pid);
        return apiDealToString(apiDeal);
    }

    public String apiDealToString(ApiDeal apiDeal) {
        return "API заявка на " + apiDeal.getDealType().getGenitive() + " №" + apiDeal.getPid() + "\n"
                + "Идентификатор клиента: " + apiDeal.getApiUser().getId() + "\n"
                + "Дата, время: " + apiDeal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n"
                + "Рекзвизиты клиента: " + apiDeal.getRequisite() + "\n"
                + "Реквизиты оплаты: " + apiDeal.getApiUser().getRequisite(apiDeal.getDealType()) + "\n"
                + "Количество сделок: " + apiDealService.countByApiDealStatusAndApiUser_Pid(ApiDealStatus.ACCEPTED, apiDeal.getApiUser().getPid()) + "\n"
                + "Сумма " + apiDeal.getCryptoCurrency().getShortName() + ": " + apiDeal.getCryptoAmount() + "\n"
                + "Сумма " + apiDeal.getApiUser().getFiatCurrency().getDisplayName() + ": " + apiDeal.getAmount();
    }

    public String dealToRequestString(Long pid) {
        Deal deal = readDealService.findByPid(pid);
        String dealString = dealToString(pid);
        if (CryptoCurrency.BITCOIN.equals(deal.getCryptoCurrency()) && DealType.BUY.equals(deal.getDealType()))
            return dealString + "\nСтрока для вывода:\n<code>" + deal.getWallet() + "," + bigDecimalService.toPlainString(deal.getCryptoAmount()) + "</code>";
        else
            return dealString;
    }


    public String dealToString(Long pid) {
        return dealToString(readDealService.findByPid(pid));
    }

    private static final String DEAL_INFO = "Заявка на %s №%s \n" + "Дата,время: %s\n" + "Тип оплаты: %s\n" + "Кошелек: %s\n" + "Контакт: %s\n"
            + "Количество сделок: %s\n" + "ID: %s\n" + "Курс: %s\n" + "Сумма %s: %s\n" + "Сумма: %s %s\n" + "Способ доставки: %s";

    public String dealToString(Deal deal) {
        User user = deal.getUser();
        String paymentTypeName = Objects.nonNull(deal.getPaymentType()) ? deal.getPaymentType().getName() : "Не установлен тип оплаты.";
        FiatCurrency fiatCurrency = deal.getFiatCurrency();
        return String.format(
                DEAL_INFO, deal.getDealType().getGenitive(), deal.getPid(),
                deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                paymentTypeName,
                deal.getWallet(),
                StringUtils.defaultIfEmpty(readUserService.getUsernameByChatId(user.getChatId()),
                        "Отсутствует"),
                dealCountService.getCountPassedByUserChatId(user.getChatId()), user.getChatId(),
                deal.getCourse(),
                deal.getCryptoCurrency().getShortName(),
                deal.getCryptoAmount().setScale(8, RoundingMode.FLOOR).stripTrailingZeros()
                        .toPlainString(),
                deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
                Objects.nonNull(fiatCurrency) ? fiatCurrency.getGenitive() : "отсутствует",
                Objects.nonNull(deal.getDeliveryType()) ? deliveryTypeService.getDisplayName(deal.getDeliveryType()) : "Отсутствует"
        );
    }

    public ReplyKeyboard dealToStringButtons(Long pid) {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text(commandService.getText(Command.CONFIRM_USER_DEAL))
                .data(callbackQueryService.buildCallbackData(Command.CONFIRM_USER_DEAL, new Object[]{pid, false}))
                .build());
        boolean hasDefaultGroupChat = groupChatService.hasDealRequests();
        if (hasDefaultGroupChat)
            buttons.add(InlineButton.builder()
                    .text(commandService.getText(Command.CONFIRM_USER_DEAL) + "с запросом")
                    .data(callbackQueryService.buildCallbackData(Command.CONFIRM_USER_DEAL, new Object[]{pid, true}))
                    .build());
        buttons.add(InlineButton.builder()
                .text(commandService.getText(Command.ADDITIONAL_VERIFICATION))
                .data(callbackQueryService.buildCallbackData(Command.ADDITIONAL_VERIFICATION, pid))
                .build());
        buttons.add(InlineButton.builder()
                .text(commandService.getText(Command.DELETE_DEAL))
                .data(callbackQueryService.buildCallbackData(Command.DELETE_USER_DEAL, pid))
                .build());
        buttons.add(InlineButton.builder()
                .text(commandService.getText(Command.DELETE_DEAL_AND_BLOCK_USER))
                .data(callbackQueryService.buildCallbackData(Command.DELETE_DEAL_AND_BLOCK_USER, pid))
                .build());
        return keyboardBuildService.buildInline(buttons, 2);
    }

}
