package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.math.BigDecimal;

@Service
public class ChangeMinSumStateHandler implements IStateHandler {

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IResponseSender responseSender;

    private final IAdminPanelService adminPanelService;

    private final IPaymentTypeService paymentTypeService;

    private final IBigDecimalService bigDecimalService;

    public ChangeMinSumStateHandler(IRedisStringService redisStringService, IRedisUserStateService redisUserStateService,
                                    IResponseSender responseSender, IAdminPanelService adminPanelService,
                                    IPaymentTypeService paymentTypeService, IBigDecimalService bigDecimalService) {
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.responseSender = responseSender;
        this.adminPanelService = adminPanelService;
        this.paymentTypeService = paymentTypeService;
        this.bigDecimalService = bigDecimalService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите новое значение минимальной суммы, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisStringService.delete(RedisPrefix.PAYMENT_TYPE_PID, chatId);
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        BigDecimal minSum;
        try {
            minSum = BigDecimal.valueOf(Double.parseDouble(text));
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Введите валидное значение.");
            return;
        }
        Long paymentTypePid = Long.parseLong(redisStringService.get(RedisPrefix.PAYMENT_TYPE_PID, chatId));
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        paymentType.setMinSum(minSum);
        paymentTypeService.save(paymentType);
        redisStringService.delete(RedisPrefix.PAYMENT_TYPE_PID, chatId);
        redisUserStateService.delete(chatId);
        responseSender.sendMessage(chatId, "Минимальная сумма для типа оплаты <b>" + paymentType.getName()
                + "</b> обновлена. Новое значение: <b>" + bigDecimalService.roundToPlainString(minSum, 2) + "</b>");
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.CHANGE_MIN_SUM;
    }
}
