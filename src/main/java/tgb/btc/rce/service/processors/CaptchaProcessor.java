package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.schedule.CaptchaSender;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CAPTCHA)
public class CaptchaProcessor extends Processor {

    private CaptchaSender captchaSender;

    private Start start;

    private AntiSpam antiSpam;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Autowired
    public CaptchaProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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
                if (UpdateUtil.getMessageText(update).equals(AntiSpam.CAPTCHA_CASH.get(chatId))) {
                    antiSpam.removeUser(chatId);
                    AntiSpam.CAPTCHA_CASH.remove(chatId);
                    start.run(chatId);
                } else send(chatId);
                break;
            case 3:
                userService.ban(chatId);
                responseSender.sendMessage(chatId, "Вы были заблокированы.");
                userService.setDefaultValues(chatId);
                antiSpam.removeUser(chatId);
                break;
        }
    }

    public void send(Long chatId) {
        captchaSender.sendCaptcha(chatId);
        userService.nextStep(chatId, Command.CAPTCHA);
    }
}