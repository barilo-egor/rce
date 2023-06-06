package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.lib.bean.BotVariable;
import tgb.btc.lib.constants.ErrorMessage;
import tgb.btc.lib.enums.BotVariableType;
import tgb.btc.lib.exception.BaseException;
import tgb.btc.lib.repository.BaseRepository;
import tgb.btc.lib.repository.BotVariableRepository;

@Service
public class BotVariableService extends BasePersistService<BotVariable> {
    private final BotVariableRepository botVariableRepository;

    @Autowired
    public BotVariableService(BaseRepository<BotVariable> baseRepository, BotVariableRepository botVariableRepository) {
        super(baseRepository);
        this.botVariableRepository = botVariableRepository;
    }

    public BotVariable findByType(BotVariableType type) {
        return botVariableRepository.findByType(type).orElseThrow(
                () -> new BaseException(String.format(ErrorMessage.BOT_VARIABLE_NOT_FOUND, type.getDisplayName())));
    }

    public Double getDoubleByType(BotVariableType type) {
        return Double.valueOf(findByType(type).getValue());
    }
}
