package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import tgb.btc.rce.conditional.AntispamCondition;
import tgb.btc.rce.service.ICaptchaSender;
import tgb.btc.rce.service.captcha.ICaptchaService;

@Service
@Conditional(AntispamCondition.class)
public class CaptchaSender implements ICaptchaSender {

    private ICaptchaService captchaService;

    @Autowired
    public void setCaptchaService(ICaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public void sendCaptcha(Long chatId) {
        captchaService.send(chatId);
    }
}
