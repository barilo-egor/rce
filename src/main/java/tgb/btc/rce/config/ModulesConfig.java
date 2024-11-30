package tgb.btc.rce.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tgb.btc.rce.service.impl.calculator.InlineCalculatorService;
import tgb.btc.rce.service.impl.calculator.InlineQueryCalculatorService;
import tgb.btc.rce.service.impl.calculator.NoneCalculatorService;

@Configuration
public class ModulesConfig {

    @Bean
    @ConditionalOnExpression("'${calculator.type}' == 'INLINE_QUERY'")
    public InlineQueryCalculatorService inlineQueryCalculatorService() {
        return new InlineQueryCalculatorService();
    }

    @Bean
    @ConditionalOnExpression("'${calculator.type}' == 'NONE'")
    public NoneCalculatorService noneCalculatorService() {
        return new NoneCalculatorService();
    }

    @Bean
    @ConditionalOnExpression("'${calculator.type}' == 'INLINE'")
    public InlineCalculatorService inlineCalculatorService() {
        return new InlineCalculatorService();
    }

}
