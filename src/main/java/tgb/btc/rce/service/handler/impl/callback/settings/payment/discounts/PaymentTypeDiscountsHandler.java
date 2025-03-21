package tgb.btc.rce.service.handler.impl.callback.settings.payment.discounts;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.bean.bot.PaymentTypeDiscount;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentTypeDiscountsHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeDiscountService paymentTypeDiscountService;

    private final IKeyboardBuildService keyboardBuildService;

    private final IResponseSender responseSender;

    private final IPaymentTypeService paymentTypeService;

    private final IMenuSender menuSender;

    public PaymentTypeDiscountsHandler(ICallbackDataService callbackDataService,
                                       IPaymentTypeDiscountService paymentTypeDiscountService,
                                       IKeyboardBuildService keyboardBuildService, IResponseSender responseSender,
                                       IPaymentTypeService paymentTypeService, IMenuSender menuSender) {
        this.callbackDataService = callbackDataService;
        this.paymentTypeDiscountService = paymentTypeDiscountService;
        this.keyboardBuildService = keyboardBuildService;
        this.responseSender = responseSender;
        this.paymentTypeService = paymentTypeService;
        this.menuSender = menuSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        sendPaymentTypeDiscounts(chatId, paymentTypePid);
    }

    public void sendPaymentTypeDiscounts(Long chatId, Long paymentTypePid) {
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        List<PaymentTypeDiscount> discountList = paymentTypeDiscountService.getByPaymentTypePid(paymentTypePid);
        List<InlineButton> buttons = new ArrayList<>();
        for (PaymentTypeDiscount paymentTypeDiscount : discountList) {
            buttons.add(InlineButton.builder()
                    .text(paymentTypeDiscount.getPercent() + "% до " + paymentTypeDiscount.getMaxAmount() + FiatCurrency.RUB.getGenitive())
                    .data("none")
                    .build());
            buttons.add(InlineButton.builder()
                    .text("⬅\uFE0F удалить ❌")
                    .data(callbackDataService.buildData(CallbackQueryData.DELETE_PAYMENT_TYPE_DISCOUNT, paymentTypeDiscount.getPid()))
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text("Добавить")
                .data(callbackDataService.buildData(CallbackQueryData.NEW_PAYMENT_TYPE_DISCOUNT, paymentTypePid))
                .build()
        );
        ReplyKeyboard keyboard = keyboardBuildService.buildInline(buttons, 2);
        responseSender.sendMessage(chatId, "Скидки типа оплаты <b>\"" + paymentType.getName() + "\"</b>.", keyboard);
        menuSender.send(chatId, "Меню управления типами оплаты.", Menu.PAYMENT_TYPES);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PAYMENT_TYPE_DISCOUNTS;
    }
}
