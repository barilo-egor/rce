package tgb.btc.lib.repository;

import org.springframework.stereotype.Repository;
import tgb.btc.lib.bean.BotVariable;
import tgb.btc.lib.enums.BotVariableType;

import java.util.Optional;

@Repository
public interface BotVariableRepository extends BaseRepository<BotVariable> {
    Optional<BotVariable> findByType(BotVariableType type);
}