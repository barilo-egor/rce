package tgb.btc.rce.service.handler.util;

import tgb.btc.rce.enums.update.TextMessageType;

public interface ITextMessageTypeService {
    TextMessageType fromText(String text);
}
