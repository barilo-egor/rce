package tgb.btc.rce.service.impl.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import tgb.btc.rce.conditional.AntispamCondition;
import tgb.btc.rce.service.ICaptchaService;

@Service
@Conditional(AntispamCondition.class)
public class CaptchaSender {

    private ICaptchaService captchaService;

    @Autowired
    public void setCaptchaService(ICaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public void sendCaptcha(Long chatId) {
        captchaService.send(chatId);
    }
}
