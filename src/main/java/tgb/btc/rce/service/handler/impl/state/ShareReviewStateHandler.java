package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShareReviewStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IRedisStringService redisStringService;

    private final IRedisUserStateService redisUserStateService;

    private final IStartService startService;

    public ShareReviewStateHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                   IRedisStringService redisStringService, IRedisUserStateService redisUserStateService,
                                   IStartService startService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.redisStringService = redisStringService;
        this.redisUserStateService = redisUserStateService;
        this.startService = startService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Введите ваш отзыв, либо нажмите \""
                    + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (message.getText().equals(TextCommand.CANCEL.getText())) {
            redisStringService.delete(chatId);
            redisUserStateService.delete(chatId);
            startService.processToMainMenu(chatId);
            return;
        }
        Integer messageId = Integer.parseInt(redisStringService.get(RedisPrefix.MESSAGE_ID, chatId));
        responseSender.deleteMessage(chatId, messageId);
        Long dealPid = Long.parseLong(redisStringService.get(RedisPrefix.DEAL_PID, chatId));
        String text = message.getText();
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text("Оставить публично")
                .data(callbackDataService.buildData(CallbackQueryData.SUBMIT_SHARE_REVIEW, true, dealPid))
                .build());
        buttons.add(InlineButton.builder()
                .text("Оставить анонимно")
                .data(callbackDataService.buildData(CallbackQueryData.SUBMIT_SHARE_REVIEW, false, dealPid))
                .build());
        buttons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());
        redisUserStateService.delete(chatId);
        redisStringService.delete(RedisPrefix.MESSAGE_ID, chatId);
        redisStringService.delete(RedisPrefix.DEAL_PID, chatId);
        responseSender.sendMessage(chatId, "<b>Ваш отзыв:</b>");
        responseSender.sendMessage(chatId, text, buttons);
        startService.processToMainMenu(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.SHARE_REVIEW;
    }
}
