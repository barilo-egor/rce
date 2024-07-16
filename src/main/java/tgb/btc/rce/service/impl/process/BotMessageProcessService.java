package tgb.btc.rce.service.impl.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.BotMessage;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.constants.enums.bot.MessageType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.IBotMessageService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.process.IBotMessageProcessService;
import tgb.btc.rce.service.util.IBotImageService;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BotMessageProcessService implements IBotMessageProcessService {

    private IBotMessageService botMessageService;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private ResponseSender responseSender;

    private IKeyboardBuildService keyboardBuildService;

    private IBotImageService botImageService;

    @Autowired
    public void setBotImageService(IBotImageService botImageService) {
        this.botImageService = botImageService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Autowired
    public void setBotMessageService(IBotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void askForType(Long chatId) {
        modifyUserService.nextStep(chatId, Command.BOT_MESSAGES.name());

        BotMessageType[] values = BotMessageType.values();

        List<ReplyButton> buttons = Arrays.stream(values)
                .map(t -> ReplyButton.builder().text(t.getDisplayName()).build())
                .collect(Collectors.toList());
        buttons.add(ReplyButton.builder().text(Command.ADMIN_BACK.getText()).build());
        responseSender.sendMessage(chatId, "Выберите сообщение для замены.",
                keyboardBuildService.buildReply(2, buttons, true));
    }

    @Override
    public void askForNewValue(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        BotMessageType type;
        try {
            type = BotMessageType.getByDisplayName(UpdateUtil.getMessageText(update));
        } catch (BaseException e) {
            responseSender.sendMessage(chatId, "Тип сообщения не найден.");
            return;
        }
        modifyUserService.updateBufferVariable(chatId, type.name());
        responseSender.sendMessage(chatId, "Отправьте новое сообщение.");
        modifyUserService.nextStep(chatId);
    }

    @Override
    public void updateValue(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        BotMessage botMessage;
        BotMessageType type = BotMessageType.getByName(readUserService.getBufferVariable(chatId));
        try {
            botMessage = botMessageService.findByTypeThrows(type);
        } catch (BaseException e) {
            botMessage = new BotMessage();
            botMessage.setType(type);
        }

        if (update.getMessage().hasPhoto()) {
            botMessage.setMessageType(MessageType.IMAGE);
            botMessage.setImage(botImageService.getImageId(update.getMessage().getPhoto()));
            botMessage.setText(update.getMessage().getCaption());
        } else if (update.getMessage().hasAnimation()) {
            botMessage.setMessageType(MessageType.ANIMATION);
            botMessage.setAnimation(update.getMessage().getAnimation().getFileId());
            botMessage.setText(update.getMessage().getCaption());
        } else {
            botMessage.setMessageType(MessageType.TEXT);
            botMessage.setText(update.getMessage().getText());
        }
        botMessageService.save(botMessage);
        responseSender.sendMessage(chatId, "Сообщение обновлено.");
    }
}
