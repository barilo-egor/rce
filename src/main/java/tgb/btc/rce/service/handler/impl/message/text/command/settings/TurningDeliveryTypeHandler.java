package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;

import java.util.List;

@Service
public class TurningDeliveryTypeHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    private final IKeyboardService keyboardService;

    public TurningDeliveryTypeHandler(IResponseSender responseSender,
                                      IKeyboardBuildService keyboardBuildService, IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendMessage(chatId, "Включение/выключение способов доставки.",
                keyboardBuildService.buildInline(List.of(keyboardService.getDeliveryTypeButton())));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.TURNING_DELIVERY_TYPE;
    }
}
