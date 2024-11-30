package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.io.File;

@Service
public class BotVariablesHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IKeyboardService keyboardService;

    public BotVariablesHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                               IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        redisUserStateService.save(chatId, UserState.BOT_VARIABLES);
        responseSender.sendMessage(chatId, "Измените нужные значения и отправьте исправленный файл. " +
                "Обязательно закройте файл, перед тем как отправлять.", keyboardService.getReplyCancel());
        responseSender.sendFile(chatId, new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName()));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BOT_VARIABLES;
    }
}
