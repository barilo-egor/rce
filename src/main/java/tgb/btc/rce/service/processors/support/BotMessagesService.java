package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.BotMessage;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.constants.enums.bot.MessageType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.repository.bot.UserRepository;
import tgb.btc.library.service.bean.bot.BotMessageService;
import tgb.btc.library.service.bean.bot.UserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.sender.ResponseSender;
import tgb.btc.rce.util.BotImageUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BotMessagesService {

    private final UserService userService;
    private final ResponseSender responseSender;
    private final BotMessageService botMessageService;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public BotMessagesService(UserService userService, ResponseSender responseSender,
                              BotMessageService botMessageService) {
        this.userService = userService;
        this.responseSender = responseSender;
        this.botMessageService = botMessageService;
    }

    public void askForType(Long chatId, Command command) {
        userRepository.nextStep(chatId, Command.BOT_MESSAGES.name());

        BotMessageType[] values = BotMessageType.values();

        List<ReplyButton> buttons = Arrays.stream(values)
                .map(t -> ReplyButton.builder().text(t.getDisplayName()).build())
                .collect(Collectors.toList());
        buttons.add(ReplyButton.builder().text(Command.ADMIN_BACK.getText()).build());
        responseSender.sendMessage(chatId, "Выберите сообщение для замены.",
                KeyboardUtil.buildReply(2, buttons, true));
    }

    public void askForNewValue(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        BotMessageType type;
        try {
            type = BotMessageType.getByDisplayName(UpdateUtil.getMessageText(update));
        } catch (BaseException e) {
            responseSender.sendMessage(chatId, "Тип сообщения не найден.");
            return;
        }
        userService.updateBufferVariable(chatId, type.name());
        responseSender.sendMessage(chatId, "Отправьте новое сообщение.");
        userService.nextStep(chatId);
    }

    public void updateValue(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        BotMessage botMessage;
        BotMessageType type = BotMessageType.getByName(userService.getBufferVariable(chatId));
        try {
            botMessage = botMessageService.findByTypeThrows(type);
        } catch (BaseException e) {
            botMessage = new BotMessage();
            botMessage.setType(type);
        }

        if (update.getMessage().hasPhoto()) {
            botMessage.setMessageType(MessageType.IMAGE);
            botMessage.setImage(BotImageUtil.getImageId(update.getMessage().getPhoto()));
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
