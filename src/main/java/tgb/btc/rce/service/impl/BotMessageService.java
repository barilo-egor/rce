package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.BotMessage;
import tgb.btc.rce.constants.ErrorMessage;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.MessageType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.BotMessageRepository;

@Service
public class BotMessageService extends BasePersistService<BotMessage> {
    private final BotMessageRepository botMessageRepository;

    @Autowired
    public BotMessageService(BaseRepository<BotMessage> baseRepository, BotMessageRepository botMessageRepository) {
        super(baseRepository);
        this.botMessageRepository = botMessageRepository;
    }

    public BotMessage findByType(BotMessageType botMessageType) {
        return botMessageRepository.findByType(botMessageType)
                .orElse(BotMessage.builder().messageType(MessageType.TEXT)
                        .text(String.format(ErrorMessage.BOT_MESSAGE_NOT_SET, botMessageType.getDisplayName())).build());
    }

    public BotMessage findByTypeThrows(BotMessageType botMessageType) {
        return botMessageRepository.findByType(botMessageType)
                .orElseThrow(() -> new BaseException(String.format(ErrorMessage.BOT_MESSAGE_NOT_SET,
                        botMessageType.getDisplayName())));
    }
}

