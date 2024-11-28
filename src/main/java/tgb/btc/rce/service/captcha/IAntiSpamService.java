package tgb.btc.rce.service.captcha;

public interface IAntiSpamService {

    boolean isSpam(Long chatId);
}
