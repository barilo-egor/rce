package tgb.btc.rce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.AntiSpamType;
import tgb.btc.rce.enums.CalculatorType;
import tgb.btc.rce.service.impl.CaptchaSender;
import tgb.btc.rce.service.impl.calculator.InlineCalculatorService;
import tgb.btc.rce.service.impl.calculator.InlineQueryCalculatorService;
import tgb.btc.rce.service.impl.calculator.NoneCalculatorService;
import tgb.btc.rce.service.impl.captcha.AntiSpam;
import tgb.btc.rce.service.impl.captcha.EmojiCaptchaService;
import tgb.btc.rce.service.impl.captcha.PictureCaptchaService;

@Configuration
public class ModulesConfig {

    private IModule<CalculatorType> calculatorModule;

    private IModule<AntiSpamType> antiSpamModule;

    @Autowired
    public void setAntiSpamModule(IModule<AntiSpamType> antiSpamModule) {
        this.antiSpamModule = antiSpamModule;
    }

    @Autowired
    public void setCalculatorModule(IModule<CalculatorType> calculatorModule) {
        this.calculatorModule = calculatorModule;
    }

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

    @Bean
    @ConditionalOnExpression("'${anti.spam}' != 'NONE'")
    public AntiSpam antiSpam() {
        if (antiSpamModule.isCurrent(AntiSpamType.NONE))
            return null;
        return new AntiSpam();
    }

    @Bean
    @ConditionalOnExpression("'${anti.spam}' != 'NONE'")
    public CaptchaSender captchaSender() {
        if (antiSpamModule.isCurrent(AntiSpamType.NONE))
            return null;
        return new CaptchaSender();
    }

    @Bean
    @ConditionalOnProperty(value = "anti.spam", havingValue = "EMOJI")
    public EmojiCaptchaService emojiCaptchaService() {
        if (antiSpamModule.isCurrent(AntiSpamType.EMOJI))
            return new EmojiCaptchaService();
        return null;
    }

    @Bean
    @ConditionalOnProperty(value = "anti.spam", havingValue = "PICTURE")
    public PictureCaptchaService pictureCaptchaService() {
        if (antiSpamModule.isCurrent(AntiSpamType.PICTURE))
            return new PictureCaptchaService();
        return null;
    }

}
