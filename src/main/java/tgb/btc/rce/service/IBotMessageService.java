package tgb.btc.rce.service;

import tgb.btc.rce.bean.BotMessage;
import tgb.btc.rce.enums.BotMessageType;

public interface IBotMessageService {
    BotMessage findByType(BotMessageType botMessageType);

    BotMessage findByTypeThrows(BotMessageType botMessageType);
}
