package tgb.btc.rce.service.handler.impl.state.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
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

@Service
public class EnterDealRequisiteStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IMenuSender menuSender;

    private final ICallbackDataService callbackDataService;

    public EnterDealRequisiteStateHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                          IRedisStringService redisStringService, IMenuSender menuSender,
                                          ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.menuSender = menuSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Введите реквизиты либо нажмите \"Отмена\".");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (BotReplyButton.CANCEL.getText().equals(text)) {
            redisStringService.delete(RedisPrefix.DEAL_PID, chatId);
            redisUserStateService.delete(chatId);
            menuSender.send(chatId, "Меню заявок.", Menu.REQUESTS);
            return;
        }
        long dealPid = Long.parseLong(redisStringService.get(RedisPrefix.DEAL_PID, chatId));
        int messageId = Integer.parseInt(redisStringService.get(RedisPrefix.MESSAGE_ID, chatId));
        responseSender.sendMessage(chatId, "Предварительный просмотр нового реквизита для сделки <b>№" + dealPid + "</b>:");
        responseSender.sendMessage(chatId, text,
                InlineButton.builder().text("Сохранить").data(callbackDataService.buildData(CallbackQueryData.SAVE_DEAL_REQUISITE, dealPid, messageId)).build(),
                InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build()
        );
        redisStringService.delete(RedisPrefix.DEAL_PID, chatId);
        redisUserStateService.delete(chatId);
        menuSender.send(chatId, "Меню заявок.", Menu.REQUESTS);
    }

    @Override
    public UserState getUserState() {
        return UserState.ENTER_DEAL_REQUISITE;
    }
}
