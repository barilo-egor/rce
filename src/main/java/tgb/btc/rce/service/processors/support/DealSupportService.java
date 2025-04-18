package tgb.btc.rce.service.processors.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.bean.bot.SecurePaymentDetails;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.bot.*;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.interfaces.enums.IDeliveryTypeService;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.ISecurePaymentDetailsService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.HTMLTag;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DealSupportService {

    private static final String ABSENT = "Отсутствует";

    private IReadUserService readUserService;

    private IApiDealService apiDealService;

    private IReadDealService readDealService;

    private IDealCountService dealCountService;

    private IGroupChatService groupChatService;

    private IKeyboardBuildService keyboardBuildService;

    private IDeliveryTypeService deliveryTypeService;

    private IBigDecimalService bigDecimalService;

    private ISecurePaymentDetailsService securePaymentDetailsService;

    private ICryptoWithdrawalService cryptoWithdrawalService;

    private ICallbackDataService callbackDataService;

    private IResponseSender responseSender;

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setCryptoWithdrawalService(ICryptoWithdrawalService cryptoWithdrawalService) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Autowired
    public void setSecurePaymentDetailsService(ISecurePaymentDetailsService securePaymentDetailsService) {
        this.securePaymentDetailsService = securePaymentDetailsService;
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
        ApiDeal apiDeal = apiDealService.getByPid(pid);
        String dealString = apiDealToString(apiDeal);
        if (CryptoCurrency.BITCOIN.equals(apiDeal.getCryptoCurrency()) && DealType.BUY.equals(apiDeal.getDealType()))
            return dealString + "\nСтрока для вывода:\n<code>" + apiDeal.getRequisite() + ","
                    + bigDecimalService.toPlainString(apiDeal.getCryptoAmount()) + "</code>";
        else
            return dealString;
    }

    public String apiDealToString(ApiDeal apiDeal) {
        String apiDealType = ApiDealType.API.equals(apiDeal.getApiDealType())
                ? "API заявка"
                : "Диспут";
        String requisite;
        if (Objects.nonNull(apiDeal.getApiRequisite())) {
            requisite = apiDeal.getApiRequisite().getRequisite();
        } else {
            requisite = ABSENT;
        }
        return apiDealType + " на " + apiDeal.getDealType().getGenitive() + " №" + apiDeal.getPid() + "\n"
                + "Идентификатор клиента: " + apiDeal.getApiUser().getId() + "\n"
                + "Дата, время: " + apiDeal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n"
                + (Objects.nonNull(apiDeal.getApiPaymentType())
                ? "Тип оплаты:" + apiDeal.getApiPaymentType().getName() + "(" + apiDeal.getApiPaymentType().getId() + ")"
                : "") + "\n"
                + "Реквизиты клиента: " + apiDeal.getRequisite() + "\n"
                + "Реквизиты оплаты: " + requisite + "\n"
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

    private static final String DEAL_INFO = """
            Заявка на %s №%s\s
            Дата,время: %s
            Тип оплаты: %s
            Кошелек: %s
            Контакт: %s
            Количество сделок: %s
            ID: %s
            Курс: %s
            Сумма %s: %s
            Сумма: %s %s
            Способ доставки: %s
            Реквизит: %s""";

    public String dealToString(Deal deal) {
        User user = deal.getUser();
        SecurePaymentDetails securePaymentDetails = securePaymentDetailsService.hasAccessToPaymentTypes(user.getChatId(), deal.getFiatCurrency()) || !DealType.isBuy(deal.getDealType())
                ? null
                : securePaymentDetailsService.getByChatIdAndFiatCurrency(user.getChatId(), deal.getFiatCurrency());
        String paymentTypeName;
        if (Objects.nonNull(deal.getPaymentType())) {
            paymentTypeName = deal.getPaymentType().getName();
        } else {
            paymentTypeName = Objects.nonNull(securePaymentDetails)
                    ? "защитный реквизит"
                    : "Не установлен тип оплаты.";
        }
        FiatCurrency fiatCurrency = deal.getFiatCurrency();
        String dealInfo = DEAL_INFO;
        if (Objects.nonNull(deal.getMerchant())) {
            String merchantString = "\uD83D\uDCB3 \nСделка мерчанта <b>" + deal.getMerchant().getDisplayName()
                    + "</b>\nСтатус ордера: <b>"
                    + deal.getMerchant().getGetDisplayStatusFunction().apply(deal.getMerchantOrderStatus())  + "</b>\n"
                    + "ID ордера: " + HTMLTag.CODE.wrap(deal.getMerchantOrderId()) + "\n"
                    + "\n========\n\n";
            dealInfo = merchantString + dealInfo;
        }
        String dealAmount = bigDecimalService.roundToPlainString(deal.getAmount());
        return String.format(
                dealInfo, deal.getDealType().getGenitive(), deal.getPid(),
                deal.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                paymentTypeName,
                deal.getWallet(),
                StringUtils.defaultIfEmpty(readUserService.getUsernameByChatId(user.getChatId()),
                        ABSENT),
                dealCountService.getCountConfirmedByUserChatId(user.getChatId()), user.getChatId(),
                Objects.nonNull(deal.getCourse())
                        ? bigDecimalService.roundToPlainString(deal.getCourse(), 0)
                        : ABSENT,
                deal.getCryptoCurrency().getShortName(),
                deal.getCryptoAmount().setScale(8, RoundingMode.FLOOR).stripTrailingZeros()
                        .toPlainString(),
                dealAmount,
                Objects.nonNull(fiatCurrency) ? fiatCurrency.getGenitive() : ABSENT,
                Objects.nonNull(deal.getDeliveryType()) ? deliveryTypeService.getDisplayName(deal.getDeliveryType()) : ABSENT,
                StringUtils.isNotEmpty(deal.getDetails()) ? deal.getDetails() : ABSENT
        );
    }

    public ReplyKeyboard dealToStringButtons(Long pid) {
        return dealToStringButtons(readDealService.findByPid(pid));
    }

    public ReplyKeyboard dealToStringButtons(Deal deal) {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text("Подтвердить")
                .data(callbackDataService.buildData(CallbackQueryData.CONFIRM_USER_DEAL, deal.getPid(), false))
                .build());
        boolean hasDefaultGroupChat = groupChatService.hasDealRequests();
        if (hasDefaultGroupChat)
            buttons.add(InlineButton.builder()
                    .text("Подтвердить с запросом")
                    .data(callbackDataService.buildData(CallbackQueryData.CONFIRM_USER_DEAL, deal.getPid(), true))
                    .build());
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        if (DealType.isBuy(deal.getDealType()) && cryptoWithdrawalService.isOn(cryptoCurrency)) {
            buttons.add(InlineButton.builder()
                    .text("Автовывод")
                    .data(callbackDataService.buildData(CallbackQueryData.AUTO_WITHDRAWAL_DEAL, deal.getPid()))
                    .build());
        }
        if (DealType.isBuy(deal.getDealType()) && CryptoCurrency.BITCOIN.equals(cryptoCurrency) && cryptoWithdrawalService.isOn(CryptoCurrency.BITCOIN)) {
            buttons.add(InlineButton.builder()
                    .text("Добавить в пул")
                    .data(callbackDataService.buildData(CallbackQueryData.ADD_TO_POOL, deal.getPid()))
                    .build());
        }
        if (!DealType.isBuy(deal.getDealType())) {
            buttons.add(InlineButton.builder()
                    .text("DashPay вывод")
                    .data(callbackDataService.buildData(CallbackQueryData.DASH_PAY_PAY_OUT, deal.getPid()))
                    .build());
            buttons.add(InlineButton.builder()
                    .text("Ввести реквизит")
                    .data(callbackDataService.buildData(CallbackQueryData.ENTER_DEAL_REQUISITE, deal.getPid()))
                    .build());
        }

        buttons.add(InlineButton.builder()
                .text("Доп.верификация")
                .data(callbackDataService.buildData(CallbackQueryData.ADDITIONAL_VERIFICATION, deal.getPid()))
                .build());
        buttons.add(InlineButton.builder()
                .text("Удалить")
                .data(callbackDataService.buildData(CallbackQueryData.DELETE_USER_DEAL, deal.getPid()))
                .build());
        buttons.add(InlineButton.builder()
                .text("Удалить и заблокировать")
                .data(callbackDataService.buildData(CallbackQueryData.DELETE_DEAL_AND_BLOCK_USER, deal.getPid()))
                .build());
        return keyboardBuildService.buildInline(buttons, 2);
    }

    public void sendDeal(Long chatId, UserRole userRole, Long dealPid) {
        sendDeal(chatId, userRole, readDealService.findByPid(dealPid));
    }

    public void sendDeal(Long chatId, UserRole userRole, Deal deal) {
        String dealInfo = dealToString(deal.getPid());
        if (UserRole.OPERATOR_ACCESS.contains(userRole) && DealStatus.CONFIRMED.equals(deal.getDealStatus())) {
            dealInfo = "<b>===СДЕЛКА УЖЕ ПОДТВЕРЖДЕНА===</b>" + dealInfo;
        }
        if (!UserRole.OPERATOR_ACCESS.contains(userRole)) {
            responseSender.sendMessage(chatId, dealInfo);
        } else {
            List<PaymentReceipt> paymentReceipts = readDealService.getPaymentReceipts(deal.getPid());
            ReplyKeyboard replyKeyboard = DealStatus.CONFIRMED.equals(deal.getDealStatus()) ? null : dealToStringButtons(deal);
            if (paymentReceipts.isEmpty()) {
                responseSender.sendMessage(chatId, dealInfo, replyKeyboard);
            } else {
                PaymentReceipt paymentReceipt = paymentReceipts.get(0);
                if (paymentReceipt.getReceiptFormat().equals(ReceiptFormat.PICTURE)) {
                    responseSender.sendPhoto(chatId, dealInfo, paymentReceipt.getReceipt(), replyKeyboard);
                } else {
                    responseSender.sendFile(chatId, new InputFile(paymentReceipt.getReceipt()), dealInfo, replyKeyboard);
                }
            }
        }
    }
}
