package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.properties.FunctionsPropertiesReader;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;

@Service
public class DealsCountStateHandler implements IStateHandler {

    private final FunctionsPropertiesReader functionsPropertiesReader;

    private final IRedisUserStateService redisUserStateService;

    private final IResponseSender responseSender;

    private final IAdminPanelService adminPanelService;

    public DealsCountStateHandler(FunctionsPropertiesReader functionsPropertiesReader,
                                  IRedisUserStateService redisUserStateService, IResponseSender responseSender,
                                  IAdminPanelService adminPanelService) {
        this.functionsPropertiesReader = functionsPropertiesReader;
        this.redisUserStateService = redisUserStateService;
        this.responseSender = responseSender;
        this.adminPanelService = adminPanelService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите новое количество возможных активных сделок, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (message.getText().equals(BotReplyButton.CANCEL.getText())) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        Integer count = Integer.parseInt(message.getText());
        functionsPropertiesReader.setProperty("allowed.deals.count", count);
        responseSender.sendMessage(chatId, "Количество возможных активных сделок обновлено: <b>" + count + "</b>.");
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.DEALS_COUNT;
    }
}
