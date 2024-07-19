package tgb.btc.rce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.AntiSpamType;
import tgb.btc.rce.enums.CalculatorType;
import tgb.btc.rce.service.ICalculatorTypeService;
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
    public ICalculatorTypeService calculatorTypeService() {
        switch (calculatorModule.getCurrent()) {
            case INLINE:
                return new InlineCalculatorService();
            case INLINE_QUERY:
                return new InlineQueryCalculatorService();
            default:
                return new NoneCalculatorService();
        }
    }

    @Bean
    public InlineCalculatorService inlineCalculatorService() {
        if (calculatorModule.isCurrent(CalculatorType.INLINE))
            return new InlineCalculatorService();
        return null;
    }

    @Bean
    public InlineQueryCalculatorService inlineQueryCalculatorService() {
        if (calculatorModule.isCurrent(CalculatorType.INLINE_QUERY))
            return new InlineQueryCalculatorService();
        return null;
    }

    @Bean
    public NoneCalculatorService noneCalculatorService() {
        if (calculatorModule.isCurrent(CalculatorType.NONE))
            return new NoneCalculatorService();
        return null;
    }

    @Bean
    public AntiSpam antiSpam() {
        if (antiSpamModule.isCurrent(AntiSpamType.NONE))
            return null;
        return new AntiSpam();
    }

    @Bean
    public CaptchaSender captchaSender() {
        if (antiSpamModule.isCurrent(AntiSpamType.NONE))
            return null;
        return new CaptchaSender();
    }

    @Bean
    public EmojiCaptchaService emojiCaptchaService() {
        if (antiSpamModule.isCurrent(AntiSpamType.EMOJI))
            return new EmojiCaptchaService();
        return null;
    }

    @Bean
    public PictureCaptchaService pictureCaptchaService() {
        if (antiSpamModule.isCurrent(AntiSpamType.PICTURE))
            return new PictureCaptchaService();
        return null;
    }

}
