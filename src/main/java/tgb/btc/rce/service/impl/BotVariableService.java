package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.BotVariable;
import tgb.btc.rce.constants.ErrorMessage;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.BotVariableRepository;
import tgb.btc.rce.service.IBotVariableService;

@Service
public class BotVariableService extends BasePersistService<BotVariable> implements IBotVariableService {
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
