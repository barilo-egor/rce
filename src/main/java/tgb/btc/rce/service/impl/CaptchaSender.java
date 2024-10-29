package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import tgb.btc.rce.service.ICaptchaSender;
import tgb.btc.rce.service.captcha.ICaptchaService;

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
