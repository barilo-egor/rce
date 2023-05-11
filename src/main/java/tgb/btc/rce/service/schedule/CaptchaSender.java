package tgb.btc.rce.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.impl.CaptchaService;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.vo.Captcha;

@Service
public class CaptchaSender {

    private CaptchaService captchaService;

    private UserRepository userRepository;

    private UserDataRepository userDataRepository;

    private ResponseSender responseSender;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setCaptchaService(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public void sendCaptcha(Long chatId) {
        Captcha captcha = captchaService.getRandomCaptcha();
        responseSender.sendPhoto(chatId, "Сработала антифрод система. Введите капчу, чтобы продолжить.",
                captcha.getImage());
        AntiSpam.CAPTCHA_CASH.put(chatId, captcha.getStr());
    }
}
