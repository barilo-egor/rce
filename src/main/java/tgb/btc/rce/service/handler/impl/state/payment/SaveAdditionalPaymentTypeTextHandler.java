package tgb.btc.rce.service.handler.impl.state.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaveAdditionalPaymentTypeTextHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IMenuSender menuSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    public SaveAdditionalPaymentTypeTextHandler(IResponseSender responseSender, IRedisStringService redisStringService,
                                                IRedisUserStateService redisUserStateService, IMenuSender menuSender,
                                                ICallbackDataService callbackDataService, IPaymentTypeService paymentTypeService) {
        this.responseSender = responseSender;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.menuSender = menuSender;
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Отправь новый текст, либо нажми \"" + BotReplyButton.CANCEL.getText() + "\".");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        if (messageText.equals(BotReplyButton.CANCEL.getText())) {
            redisStringService.delete(RedisPrefix.PAYMENT_TYPE_PID, chatId);
            redisUserStateService.delete(chatId);
            menuSender.send(chatId, "Меню типов оплат.", Menu.PAYMENT_TYPES);
            return;
        }
        Long paymentTypePid = Long.parseLong(redisStringService.get(RedisPrefix.PAYMENT_TYPE_PID, chatId));
        if (messageText.equals("Удалить текст")) {
            PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
            paymentType.setRequisiteAdditionalText(null);
            paymentTypeService.save(paymentType);
            responseSender.sendMessage(chatId, "Текст типа оплаты <b>\"" + paymentType.getName() + "\"</b> очищен.");
            redisStringService.delete(RedisPrefix.PAYMENT_TYPE_PID, chatId);
            redisUserStateService.delete(chatId);
            menuSender.send(chatId, "Меню типов оплат.", Menu.PAYMENT_TYPES);
            return;
        }
        responseSender.sendMessage(chatId, "<b>Предварительный просмотр текста:</b>");
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text("Сохранить")
                .data(callbackDataService.buildData(CallbackQueryData.SAVE_ADDITIONAL_PT_TEXT, paymentTypePid))
                .build()
        );
        buttons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());
        responseSender.sendMessage(chatId, messageText, buttons);
        redisUserStateService.delete(chatId);
        redisStringService.delete(RedisPrefix.PAYMENT_TYPE_PID, chatId);
        menuSender.send(chatId, "Меню типов оплат.", Menu.PAYMENT_TYPES);
    }

    @Override
    public UserState getUserState() {
        return UserState.SAVE_ADDITIONAL_PT_TEXT;
    }
}
