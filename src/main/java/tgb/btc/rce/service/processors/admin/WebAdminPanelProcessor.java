package tgb.btc.rce.service.processors.admin;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.WEB_ADMIN_PANEL)
@Slf4j
public class WebAdminPanelProcessor extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, "Для перехода в веб-админ панель нажмите на кнопку.",
                KeyboardUtil.buildInline(List.of(InlineButton.builder()
                        .text("Перейти")
                        .data(PropertiesPath.SERVER_PROPERTIES.getString("main.url"))
                        .inlineType(InlineType.URL)
                        .build())));
    }
}
