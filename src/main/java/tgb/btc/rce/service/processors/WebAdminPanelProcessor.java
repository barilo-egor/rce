package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.WebUser;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.WebUserRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.web.controller.MainWebController;

import java.util.List;
import java.util.Optional;

@CommandProcessor(command = Command.WEB_ADMIN_PANEL)
@Slf4j
public class WebAdminPanelProcessor extends Processor {

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
        String url = BotStringConstants.MAIN_URL + "/web/main?username=" + webUser.getUsername()
                + "&token=" + token + "&chatId=" + chatId;
        deleteMessageAndToken(responseSender.sendMessage(chatId, "Веб админ-панель.",
                KeyboardUtil.buildInline(List.of(InlineButton.builder()
                        .text("Перейти")
                        .data(url)
                        .inlineType(InlineType.WEB_APP)
                        .build()))));
    }

    protected void deleteMessageAndToken(Optional<Message> optionalMessage) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(10000L);
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
