package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Objects;

@Service
public class AdditionalPaymentTypeTextHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    public AdditionalPaymentTypeTextHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                            IRedisStringService redisStringService, ICallbackDataService callbackDataService,
                                            IPaymentTypeService paymentTypeService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.sendMessage(chatId, "<b>Текущий текст:</b>\n\n"
                + (Objects.isNull(paymentType.getRequisiteAdditionalText())
                ? "<b>Отсутствует.</b>"
                : paymentType.getRequisiteAdditionalText()));
        responseSender.sendMessage(chatId, "Введите новый текст.", ReplyButton.builder().text("Удалить текст").build(), BotReplyButton.CANCEL.getButton());
        redisStringService.save(RedisPrefix.PAYMENT_TYPE_PID, chatId, paymentTypePid.toString());
        redisUserStateService.save(chatId, UserState.SAVE_ADDITIONAL_PT_TEXT);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ADDITIONAL_PAYMENT_TYPE_TEXT;
    }
}
