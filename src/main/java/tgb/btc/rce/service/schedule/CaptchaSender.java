package tgb.btc.rce.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import tgb.btc.rce.conditional.AntispamCondition;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.impl.CaptchaService;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.vo.Captcha;

@Service
@Conditional(AntispamCondition.class)
public class CaptchaSender {

    private CaptchaService captchaService;

    private ResponseSender responseSender;

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }
    @Autowired
    public void setCaptchaService(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public void sendCaptcha(Long chatId) {
        Captcha captcha = captchaService.getRandomCaptcha();
        responseSender.sendPhoto(chatId, "Сработала антиспам система. Введите капчу, чтобы продолжить.",
                captcha.getImage());
        AntiSpam.CAPTCHA_CASH.put(chatId, captcha.getStr());
    }
}
