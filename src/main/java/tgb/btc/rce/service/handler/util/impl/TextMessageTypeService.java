package tgb.btc.rce.service.handler.util.impl;

import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.update.TextMessageType;
import tgb.btc.rce.service.handler.util.ITextCommandService;
import tgb.btc.rce.service.handler.util.ITextMessageTypeService;

import java.util.Objects;

@Service
public class TextMessageTypeService implements ITextMessageTypeService {

    private final ITextCommandService textCommandService;

    public TextMessageTypeService(ITextCommandService textCommandService) {
        this.textCommandService = textCommandService;
    }

    @Override
    public TextMessageType fromText(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return null;
        }
        if (text.startsWith("/")) return TextMessageType.SLASH_COMMAND;
        if (textCommandService.isTextCommand(text)) return TextMessageType.TEXT_COMMAND;
        return null;
    }
}
