package tgb.btc.rce.service.handler.util;

import tgb.btc.rce.enums.update.TextCommand;

public interface ITextCommandService {
    String getText(TextCommand textCommand);

    TextCommand fromText(String messageText);

    boolean isTextCommand(String messageText);
}
