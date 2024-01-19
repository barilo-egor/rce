package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.BotMessage;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.constants.enums.bot.MessageType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.repository.BaseRepository;
import tgb.btc.library.repository.bot.BotMessageRepository;
import tgb.btc.library.service.bean.BasePersistService;
import tgb.btc.rce.constants.ErrorMessage;

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

