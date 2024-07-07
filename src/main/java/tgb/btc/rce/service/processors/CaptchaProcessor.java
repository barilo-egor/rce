package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.SpamBan;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.conditional.AntispamCondition;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.NotifyService;
import tgb.btc.rce.service.impl.schedule.CaptchaSender;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Set;

@CommandProcessor(command = Command.CAPTCHA)
@Conditional(AntispamCondition.class)
@Slf4j
public class CaptchaProcessor extends Processor {

    private CaptchaSender captchaSender;

    private Start start;

    private AntiSpam antiSpam;

    private ISpamBanService spamBanService;

    private NotifyService notifyService;

    private BanningUserService banningUserService;

    @Autowired
    public void setBanningUserService(BanningUserService banningUserService) {
        this.banningUserService = banningUserService;
    }

    @Autowired
    public void setAdminService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setSpamBanService(ISpamBanService spamBanService) {
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
        if (!Command.CAPTCHA.name().equals(readUserService.getCommandByChatId(chatId)))
            modifyUserService.setDefaultValues(chatId);
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                if (update.hasCallbackQuery())
                    responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                send(chatId);
                break;
            case 1:
            case 2:
                if (!UpdateUtil.hasMessageText(update) && !update.hasCallbackQuery()) return;
                String cashedCaptcha = AntiSpam.CAPTCHA_CASH.get(chatId);
                if (StringUtils.isEmpty(cashedCaptcha))
                    throw new BaseException("Не найдена строка капчи в кэше.");
                if (isEnteredCaptchaIsRight(update)) {
                    removeUserFromSpam(chatId);
                } else send(chatId);
                if (update.hasCallbackQuery()) responseSender.deleteCallbackMessageIfExists(update);
                break;
            case 3:
                if (isEnteredCaptchaIsRight(update)) {
                    removeUserFromSpam(chatId);
                } else {
                    banningUserService.ban(chatId);
                    log.debug("Пользователь chatId={} был заблокирован после неправильных вводов капчи.", chatId);
                    responseSender.sendMessage(chatId, "Вы были заблокированы.", BotKeyboard.OPERATOR);
                    SpamBan spamBan = spamBanService.save(chatId);
                    notifyService.notifyMessage("Антиспам система заблокировала пользователя.",
                            KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .text("Показать")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.SHOW_SPAM_BANNED_USER.getText(), spamBan.getPid().toString())
                                            )
                                            .build()
                            )), Set.of(UserRole.ADMIN, UserRole.OPERATOR));
                    modifyUserService.setDefaultValues(chatId);
                    antiSpam.removeUser(chatId);
                }
                if (update.hasCallbackQuery()) responseSender.deleteCallbackMessageIfExists(update);
                break;
        }
    }

    private void removeUserFromSpam(Long chatId) {
        antiSpam.removeUser(chatId);
        AntiSpam.CAPTCHA_CASH.remove(chatId);
        start.run(chatId);
    }

    private boolean isEnteredCaptchaIsRight(Update update) {
        String text;
        if (UpdateUtil.hasMessageText(update)) {
            text = UpdateUtil.getMessageText(update);
        } else {
            text = CallbackQueryUtil.getSplitData(update, 1);
        }
        return text.equals(AntiSpam.CAPTCHA_CASH.get(UpdateUtil.getChatId(update)));
    }

    public void send(Long chatId) {
        captchaSender.sendCaptcha(chatId);
        modifyUserService.nextStep(chatId, Command.CAPTCHA.name());
    }
}
