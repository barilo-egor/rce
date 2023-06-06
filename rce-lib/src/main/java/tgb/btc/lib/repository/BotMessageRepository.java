package tgb.btc.lib.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.lib.bean.BotMessage;
import tgb.btc.lib.enums.BotMessageType;

import java.util.Optional;

@Repository
@Transactional
public interface BotMessageRepository extends BaseRepository<BotMessage> {
    Optional<BotMessage> findByType(BotMessageType botMessageType);
}
