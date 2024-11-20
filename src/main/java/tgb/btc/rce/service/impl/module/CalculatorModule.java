package tgb.btc.rce.service.impl.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.service.properties.ModulesPropertiesReader;
import tgb.btc.rce.enums.CalculatorType;

import java.util.Objects;

@Slf4j
@Service
public class CalculatorModule implements IModule<CalculatorType> {


    private CalculatorType current;

    private ModulesPropertiesReader modulesPropertiesReader;

    @Autowired
    public void setModulesPropertiesReader(ModulesPropertiesReader modulesPropertiesReader) {
        this.modulesPropertiesReader = modulesPropertiesReader;
    }

    @Override
    public CalculatorType getCurrent() {
        if (Objects.nonNull(current))
            return current;
        String type = modulesPropertiesReader.getString("calculator.type", CalculatorType.NONE.name());
        try {
            CalculatorType calculatorType = CalculatorType.valueOf(type);
            current = calculatorType;
            return calculatorType;
        } catch (IllegalArgumentException e) {
            String message = "В проперти calculator.type из modules.properties установлено невалидное значение.";
            log.error(message);
            throw new BaseException(message, e);
        }
    }

    @Override
    public void set(CalculatorType calculatorType) {
        modulesPropertiesReader.setProperty("calculator.type", calculatorType.name());
        current = calculatorType;
    }
}
