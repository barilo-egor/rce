package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.processors.support.EditContactsService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.net.MalformedURLException;

@Service
public class AddContactStateHandler implements IStateHandler {

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final EditContactsService editContactsService;

    private final IResponseSender responseSender;

    public AddContactStateHandler(IRedisUserStateService redisUserStateService, IAdminPanelService adminPanelService,
                                  EditContactsService editContactsService, IResponseSender responseSender) {
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
        this.editContactsService = editContactsService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Отправьте контакт, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (!message.getText().equals(TextCommand.CANCEL.getText())) {
            try {
                editContactsService.save(message.getText());
            } catch (MalformedURLException e) {
                responseSender.sendMessage(chatId, "Ошибка при проверке валидности ссылки. Проверьте данные.");
                return;
            } catch (Exception e) {
                responseSender.sendMessage(chatId, "Ошибки при попытке сохранить контакт. Проверьте данные.");
                return;
            }
            responseSender.sendMessage(chatId, "Контакт успешно добавлен.");
        }
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.ADD_CONTACT;
    }
}
