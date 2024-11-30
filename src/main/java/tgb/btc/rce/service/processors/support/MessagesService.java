package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.util.Objects;


@Service
public class MessagesService {

    private ResponseSender responseSender;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private IRedisUserStateService redisUserStateService;

    private IStartService startService;

    private IRedisStringService redisStringService;

    @Autowired
    public void setRedisStringService(IRedisStringService redisStringService) {
        this.redisStringService = redisStringService;
    }

    @Autowired
    public void setStartService(IStartService startService) {
        this.startService = startService;
    }

    @Autowired
    public void setRedisUserStateService(IRedisUserStateService redisUserStateService) {
        this.redisUserStateService = redisUserStateService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Async
    public void sendMessageToUsers(Long chatId, String text) {
        readUserService.getChatIdsForMailing()
                .forEach(userChatId -> {
                    try {
                        responseSender.sendMessageThrows(userChatId, text);
                    } catch (TelegramApiException e) {
                        if (e instanceof TelegramApiRequestException exception) {
                            if (exception.getApiResponse().contains("bot was blocked by the user")) {
                                modifyUserService.updateIsActiveByChatId(false, userChatId);
                            }
                        }
                    }
                });
        responseSender.sendMessage(chatId, "Рассылка успешно завершена.");
    }

    public void sendNoHandler(Long chatId) {
        if (Objects.isNull(chatId)) {
            return;
        }
        responseSender.sendMessage(chatId, "Что-то пошло не так. Я верну тебя в главное меню, попробуй ещё раз, пожалуйста.");
        redisUserStateService.delete(chatId);
        redisStringService.deleteAll(chatId);
        startService.processToMainMenu(chatId);
    }
}
