package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.lib.bean.BotMessage;
import tgb.btc.lib.constants.ErrorMessage;
import tgb.btc.lib.enums.BotMessageType;
import tgb.btc.lib.enums.MessageType;
import tgb.btc.lib.exception.BaseException;
import tgb.btc.lib.repository.BaseRepository;
import tgb.btc.lib.repository.BotMessageRepository;

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

