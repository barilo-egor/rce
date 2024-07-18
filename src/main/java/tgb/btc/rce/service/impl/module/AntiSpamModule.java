package tgb.btc.rce.service.impl.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.service.properties.ModulesPropertiesReader;
import tgb.btc.rce.enums.AntiSpamType;

import java.util.Objects;

@Slf4j
@Service
public class AntiSpamModule implements IModule<AntiSpamType> {

    private AntiSpamType current;

    private ModulesPropertiesReader modulesPropertiesReader;

    @Autowired
    public void setModulesPropertiesReader(ModulesPropertiesReader modulesPropertiesReader) {
        this.modulesPropertiesReader = modulesPropertiesReader;
    }


    @Override
    public AntiSpamType getCurrent() {
        if (Objects.nonNull(current))
            return current;
        String type = modulesPropertiesReader.getString("anti.spam", AntiSpamType.NONE.name());
        try {
            AntiSpamType deliveryKind = AntiSpamType.valueOf(type);
            current = deliveryKind;
            return deliveryKind;
        } catch (IllegalArgumentException e) {
            String message = "В проперти anti.spam из modules.properties установлено невалидное значение.";
            log.error(message);
            throw new BaseException(message, e);
        }
    }
}
