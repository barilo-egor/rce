package tgb.btc.rce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.CalculatorType;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.impl.calculator.InlineCalculatorService;
import tgb.btc.rce.service.impl.calculator.InlineQueryCalculatorService;
import tgb.btc.rce.service.impl.calculator.NoneCalculatorService;

@Configuration
public class CalculatorConfig {

    private IModule<CalculatorType> calculatorModule;

    @Autowired
    public void setCalculatorModule(IModule<CalculatorType> calculatorModule) {
        this.calculatorModule = calculatorModule;
    }

    @Bean
    public ICalculatorTypeService calculatorTypeService() {
        switch (calculatorModule.getCurrent()) {
            default:
                return new NoneCalculatorService();
            case INLINE:
                return new InlineCalculatorService();
            case INLINE_QUERY:
                return new InlineQueryCalculatorService();
        }
    }
}
