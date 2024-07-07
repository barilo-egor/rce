package tgb.btc.rce.service.captcha;

public interface IAntiSpam {

    void putToCaptchaCash(Long chatId, String captcha);

    String getFromCaptchaCash(Long chatId);

    void removeFromCaptchaCash(Long chatId);

    boolean isVerifiedUser(Long chatId);

    boolean isSpamUser(Long chatId);

    void saveTime(Long chatId);

    void check();

    void addUser(Long chatId);

    void removeUser(Long chatId);
}
