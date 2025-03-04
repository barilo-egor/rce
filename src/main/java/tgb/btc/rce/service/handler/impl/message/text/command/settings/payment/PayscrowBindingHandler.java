package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class PayscrowBindingHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IPaymentTypeService paymentTypeService;

    private final IKeyboardBuildService keyboardBuildService;

    private final ICallbackDataService callbackDataService;

    public PayscrowBindingHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                  IPaymentTypeService paymentTypeService, IKeyboardBuildService keyboardBuildService,
                                  ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.paymentTypeService = paymentTypeService;
        this.keyboardBuildService = keyboardBuildService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        List<InlineButton> buttons = new java.util.ArrayList<>(
                paymentTypeService.getByDealTypeAndIsOnAndFiatCurrency(DealType.BUY, true, FiatCurrency.RUB).stream()
                        .map(paymentType -> InlineButton.builder()
                                .text(paymentType.getName())
                                .data(callbackDataService.buildData(CallbackQueryData.PAYSCROW_PAYMENT_TYPE, paymentType.getPid()))
                                .inlineType(InlineType.CALLBACK_DATA)
                                .build())
                        .toList()
        );
        responseSender.sendMessage(chatId,
                "Выберите тип оплаты, к которому хотите выполнить привязку метода оплаты Payscrow. Типы оплаты отображены только для валюты \""
                        + FiatCurrency.RUB.getDisplayName() + "\".", keyboardBuildService.buildInline(buttons, 2));
        redisUserStateService.save(chatId, UserState.PAYSCROW_BINDING);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.PAYSCROW_BINDING;
    }
}
