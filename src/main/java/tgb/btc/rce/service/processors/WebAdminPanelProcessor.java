package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.web.WebUser;
import tgb.btc.library.constants.enums.properties.WebProperties;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.repository.web.WebUserRepository;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.MessageProperties;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.web.controller.MainWebController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@CommandProcessor(command = Command.WEB_ADMIN_PANEL)
@Slf4j
public class WebAdminPanelProcessor extends Processor {

    private static final Long AUTH_TIME = WebProperties.LOGIN.getLong("auth.time", 10L);

    private WebUserRepository webUserRepository;

    @Autowired
    public void setWebUserRepository(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String token = RandomStringUtils.randomAlphanumeric(40);
        MainWebController.AVAILABLE_TOKENS.put(chatId, token);
        WebUser webUser = webUserRepository.getByChatId(chatId);
        String url;
        Optional<Message> optionalMessage;
        if (Objects.nonNull(webUser)) {
            url = WebProperties.SERVER.getString("main.url") + "/web/main?username=" + webUser.getUsername()
                    + "&token=" + token + "&chatId=" + chatId;
            optionalMessage = responseSender.sendMessage(chatId, "Веб админ-панель. Доступ для автоматической авторизации открыт на " + AUTH_TIME + " секунд.",
                    KeyboardUtil.buildInline(List.of(InlineButton.builder()
                            .text("Перейти")
                            .data(url)
                            .inlineType(InlineType.WEB_APP)
                            .build())));
        } else {
            url = WebProperties.SERVER.getString("main.url") + "/web/registration?chatId=" + chatId;
            responseSender.sendMessage(chatId, MessageProperties.INFO_MESSAGE.getString("web.user.not.found"),
                    KeyboardUtil.buildInline(List.of(InlineButton.builder()
                            .text("Зарегистрироваться")
                            .data(url)
                            .inlineType(InlineType.WEB_APP)
                            .build())));
            return;
        }
        deleteMessageAndToken(optionalMessage);
    }

    protected void deleteMessageAndToken(Optional<Message> optionalMessage) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(AUTH_TIME * 1000);
            } catch (InterruptedException e) {
                throw new BaseException();
            }
            optionalMessage.ifPresent(message -> {
                responseSender.deleteMessage(message.getChatId(), message.getMessageId());
                MainWebController.AVAILABLE_TOKENS.remove(message.getChatId());
                System.out.println();
            });
        });
        thread.start();
    }
}
