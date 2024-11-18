package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class WebAdminPanelHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    public WebAdminPanelHandler(IResponseSender responseSender, IKeyboardBuildService keyboardBuildService) {
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendMessage(chatId, "Для перехода в веб-админ панель нажмите на кнопку.",
                keyboardBuildService.buildInline(List.of(InlineButton.builder()
                        .text("Перейти")
                        .data(PropertiesPath.SERVER_PROPERTIES.getString("main.url"))
                        .inlineType(InlineType.URL)
                        .build())));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.WEB_ADMIN_PANEL;
    }
}
