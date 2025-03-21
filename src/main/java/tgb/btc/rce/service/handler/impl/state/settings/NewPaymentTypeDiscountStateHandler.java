package tgb.btc.rce.service.handler.impl.state.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.bean.bot.PaymentTypeDiscount;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.impl.callback.settings.payment.discounts.PaymentTypeDiscountsHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class NewPaymentTypeDiscountStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final IRedisStringService redisStringService;

    private final IPaymentTypeService paymentTypeService;

    private final PaymentTypeDiscountsHandler paymentTypeDiscountsHandler;

    private final IPaymentTypeDiscountService paymentTypeDiscountService;

    public NewPaymentTypeDiscountStateHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                              IAdminPanelService adminPanelService, IRedisStringService redisStringService,
                                              IPaymentTypeService paymentTypeService,
                                              PaymentTypeDiscountsHandler paymentTypeDiscountsHandler,
                                              IPaymentTypeDiscountService paymentTypeDiscountService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
        this.redisStringService = redisStringService;
        this.paymentTypeService = paymentTypeService;
        this.paymentTypeDiscountsHandler = paymentTypeDiscountsHandler;
        this.paymentTypeDiscountService = paymentTypeDiscountService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Введите новое значение скидки, либо нажмите \""
                    + BotReplyButton.CANCEL.getText() + "\".");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText().replaceAll(",", ".");
        if (messageText.equals(BotReplyButton.CANCEL.getText())) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        double percent;
        int maxAmount;
        try {
            String[] values = messageText.split(" ");
            percent = Double.parseDouble(values[0]);
            maxAmount = Integer.parseInt(values[1]);
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Невалидное значение.");
            return;
        }
        Long paymentTypePid = Long.parseLong(redisStringService.get(RedisPrefix.PAYMENT_TYPE_PID, chatId));
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        PaymentTypeDiscount paymentTypeDiscount = PaymentTypeDiscount.builder()
                .paymentType(paymentType)
                .percent(percent)
                .maxAmount(maxAmount)
                .build();
        paymentTypeDiscountService.save(paymentTypeDiscount);
        responseSender.sendMessage(chatId, "Скидка <b>" + percent + " до " + maxAmount
                + "</b> для типа оплаты <b>\"" + paymentType.getName() + "\"</b> сохранена.");
        redisUserStateService.delete(chatId);
        paymentTypeDiscountsHandler.sendPaymentTypeDiscounts(chatId, paymentTypePid);
    }

    @Override
    public UserState getUserState() {
        return UserState.NEW_PAYMENT_TYPE_DISCOUNT;
    }
}
