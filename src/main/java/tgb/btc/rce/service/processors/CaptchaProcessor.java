package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.SpamBan;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.bean.bot.SpamBanService;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.conditional.AntispamCondition;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.schedule.CaptchaSender;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.CAPTCHA)
@Conditional(AntispamCondition.class)
@Slf4j
public class CaptchaProcessor extends Processor {

    private CaptchaSender captchaSender;

    private Start start;

    private AntiSpam antiSpam;

    private SpamBanService spamBanService;

    private AdminService adminService;

    private BanningUserService banningUserService;

    @Autowired
    public void setBanningUserService(BanningUserService banningUserService) {
        this.banningUserService = banningUserService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

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
        if (!Command.CAPTCHA.name().equals(userRepository.getCommandByChatId(chatId)))
            userService.setDefaultValues(chatId);
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
                    banningUserService.ban(chatId);
                    log.debug("Пользователь chatId={} был заблокирован после неправильных вводов капчи.", chatId);
                    responseSender.sendMessage(chatId, "Вы были заблокированы.", BotKeyboard.OPERATOR);
                    SpamBan spamBan = spamBanService.save(chatId);
                    adminService.notify("Антиспам система заблокировала пользователя.",
                            KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .text("Показать")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.SHOW_SPAM_BANNED_USER.getText(), spamBan.getPid().toString())
                                            )
                                            .build()
                            )));
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
        userRepository.nextStep(chatId, Command.CAPTCHA.name());
    }
}
