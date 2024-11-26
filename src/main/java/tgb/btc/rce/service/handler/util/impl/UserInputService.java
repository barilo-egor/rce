package tgb.btc.rce.service.handler.util.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class UserInputService implements IUserInputService {
    
    private final IResponseSender responseSender;
    
    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;
    
    private final IAdminPanelService adminPanelService;

    private final IReadUserService readUserService;

    public UserInputService(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                            IRedisStringService redisStringService,
                            IAdminPanelService adminPanelService, IReadUserService readUserService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.adminPanelService = adminPanelService;
        this.readUserService = readUserService;
    }

    @Override
    public boolean hasTextInput(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите chat id либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return false;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisUserStateService.delete(chatId);
            redisStringService.delete(chatId);
            adminPanelService.send(chatId);
            return false;
        }
        return true;
    }

    @Override
    public Long getInputChatId(Long chatId, String input) {
        long userChatId;
        try {
            userChatId = Long.parseLong(input);
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Введите валидный chat id.");
            return null;
        }
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь с таким chat id не найден.");
            return null;
        }
        return userChatId;
    }
}
