package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.impl.util.CallbackDataService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateRequisiteHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final IPaymentTypeService paymentTypeService;

    private final CallbackDataService callbackDataService;

    public CreateRequisiteHandler(IResponseSender responseSender, IRedisStringService redisStringService,
                                  IRedisUserStateService redisUserStateService, IAdminPanelService adminPanelService,
                                  IPaymentTypeService paymentTypeService, CallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите данные реквизита, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisStringService.delete(RedisPrefix.PAYMENT_TYPE_PID, chatId);
            redisStringService.delete(RedisPrefix.FIAT_CURRENCY, chatId);
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        String messageText = """
                Предварительный просмотр:
                
                <b>Реквизит</b>: %s
                <b>Фиатная валюта</b>: %s
                <b>Тип оплаты</b>: %s
                """;
        String requisite = message.getText();
        Long paymentTypePid = Long.parseLong(redisStringService.get(RedisPrefix.PAYMENT_TYPE_PID, chatId));
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(redisStringService.get(RedisPrefix.FIAT_CURRENCY, chatId));
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(
                InlineButton.builder().text("Сохранить").data(
                        callbackDataService.buildData(
                                CallbackQueryData.SAVE_REQUISITE,
                                paymentTypePid)
                ).build()
        );
        buttons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());
        responseSender.sendMessage(chatId, messageText.formatted(requisite, fiatCurrency.getDisplayName(), paymentType.getName()), buttons);
        redisStringService.delete(RedisPrefix.PAYMENT_TYPE_PID, chatId);
        redisStringService.delete(RedisPrefix.FIAT_CURRENCY, chatId);
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.CREATE_REQUISITE;
    }
}
