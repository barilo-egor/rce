package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.SpamBanService;
import tgb.btc.rce.service.schedule.CaptchaSender;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CAPTCHA)
public class CaptchaProcessor extends Processor {

    private CaptchaSender captchaSender;

    private Start start;

    private AntiSpam antiSpam;

    private SpamBanService spamBanService;

    @Autowired
    public void setSpamBanService(SpamBanService spamBanService) {
        this.spamBanService = spamBanService;
    }

    @Autowired
    public void setAntiSpam(AntiSpam antiSpam) {
        this.antiSpam = antiSpam;
    }

    @Autowired
    public void setStart(Start start) {
        this.start = start;
    }

    @Autowired
    public void setCaptchaSender(CaptchaSender captchaSender) {
        this.captchaSender = captchaSender;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!Command.CAPTCHA.equals(userRepository.getCommandByChatId(chatId))) userService.setDefaultValues(chatId);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                if (update.hasCallbackQuery())
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                send(chatId);
                break;
            case 1:
            case 2:
                if (!UpdateUtil.hasMessageText(update)) return;
                String cashedCaptcha = AntiSpam.CAPTCHA_CASH.get(chatId);
                if (StringUtils.isEmpty(cashedCaptcha))
                    throw new BaseException("Не найдена строка капчи в кэше.");
                if (isEnteredCaptchaIsRight(update)) {
                    removeUserFromSpam(chatId);
                } else send(chatId);
                break;
            case 3:
                if (isEnteredCaptchaIsRight(update)) {
                    removeUserFromSpam(chatId);
                } else {
                    userService.ban(chatId);
                    responseSender.sendMessage(chatId, "Вы были заблокированы.", BotKeyboard.OPERATOR);
                    spamBanService.saveAndNotifyAdmins(chatId);
                    userService.setDefaultValues(chatId);
                    antiSpam.removeUser(chatId);
                }
                break;
        }
    }

    private void removeUserFromSpam(Long chatId) {
        antiSpam.removeUser(chatId);
        AntiSpam.CAPTCHA_CASH.remove(chatId);
        start.run(chatId);
    }

    private boolean isEnteredCaptchaIsRight(Update update) {
        return UpdateUtil.getMessageText(update).equals(AntiSpam.CAPTCHA_CASH.get(UpdateUtil.getChatId(update)));
    }

    public void send(Long chatId) {
        captchaSender.sendCaptcha(chatId);
        userService.nextStep(chatId, Command.CAPTCHA);
    }
}
