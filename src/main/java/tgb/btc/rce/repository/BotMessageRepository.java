package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.BotMessage;
import tgb.btc.rce.enums.BotMessageType;

import java.util.Optional;

@Repository
public interface BotMessageRepository extends BaseRepository<BotMessage> {
    Optional<BotMessage> findByType(BotMessageType botMessageType);
}
