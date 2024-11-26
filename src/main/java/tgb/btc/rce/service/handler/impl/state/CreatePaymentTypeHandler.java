package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
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
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreatePaymentTypeHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    public CreatePaymentTypeHandler(IResponseSender responseSender, IRedisStringService redisStringService,
                                    IRedisUserStateService redisUserStateService, IAdminPanelService adminPanelService,
                                    IPaymentTypeService paymentTypeService, ICallbackDataService callbackDataService) {
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
                    "Введите название для нового типа оплаты, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisUserStateService.delete(chatId);
            redisStringService.delete(RedisPrefix.DEAL_TYPE, chatId);
            redisStringService.delete(RedisPrefix.FIAT_CURRENCY, chatId);
            adminPanelService.send(chatId);
            return;
        }
        String messageText = """
                Предварительный просмотр.
                
                <b>Название</b>: %s
                <b>Тип оплаты</b>: %s
                <b>Фиатная валюта</b>: %s
                """;
        List<InlineButton> buttons = new ArrayList<>();
        DealType dealType = DealType.valueOf(redisStringService.get(RedisPrefix.DEAL_TYPE, chatId));
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(redisStringService.get(RedisPrefix.FIAT_CURRENCY, chatId));
        buttons.add(
                InlineButton.builder()
                        .text("Сохранить")
                        .data(callbackDataService.buildData(
                                CallbackQueryData.SAVE_PAYMENT_TYPE,
                                text, dealType.name(), fiatCurrency.name()
                        ))
                        .build()
        );
        buttons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());
        responseSender.sendMessage(chatId, messageText.formatted(text, dealType.getNominativeFirstLetterToUpper(),
                fiatCurrency.getDisplayName()), buttons);
        redisStringService.delete(RedisPrefix.FIAT_CURRENCY, chatId);
        redisStringService.delete(RedisPrefix.DEAL_TYPE, chatId);
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.CREATE_PAYMENT_TYPE;
    }
}
