package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.BotVariable;
import tgb.btc.rce.enums.BotVariableType;

import java.util.Optional;

@Repository
public interface BotVariableRepository extends BaseRepository<BotVariable> {
    Optional<BotVariable> findByType(BotVariableType type);
}