package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.SpamBan;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.ICaptchaSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.captcha.IAntiSpam;
import tgb.btc.rce.service.processors.tool.Start;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Set;

@CommandProcessor(command = Command.CAPTCHA)
@Slf4j
@ConditionalOnExpression("'${anti.spam}' != 'NONE'")
public class CaptchaProcessor extends Processor {

    private ICaptchaSender captchaSender;

    private Start start;

    private IAntiSpam antiSpam;

    private ISpamBanService spamBanService;

    private INotifyService notifyService;

    private BanningUserService banningUserService;

    @Autowired
    public void setBanningUserService(BanningUserService banningUserService) {
        this.banningUserService = banningUserService;
    }

    @Autowired
    public void setAdminService(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setSpamBanService(ISpamBanService spamBanService) {
        this.spamBanService = spamBanService;
    }

    @Autowired
    public void setAntiSpam(IAntiSpam antiSpam) {
        this.antiSpam = antiSpam;
    }

    @Autowired
    public void setStart(Start start) {
        this.start = start;
    }

    @Autowired
    public void setCaptchaSender(ICaptchaSender captchaSender) {
        this.captchaSender = captchaSender;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
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
                if (!updateService.hasMessageText(update) && !update.hasCallbackQuery()) return;
                String cashedCaptcha = antiSpam.getFromCaptchaCash(chatId);
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
                    responseSender.sendMessage(chatId, "Вы были заблокированы.", keyboardService.getOperator());
                    SpamBan spamBan = spamBanService.save(chatId);
                    notifyService.notifyMessage("Антиспам система заблокировала пользователя.",
                            keyboardBuildService.buildInline(List.of(
                                    InlineButton.builder()
                                            .text(commandService.getText(Command.SHOW_SPAM_BANNED_USER))
                                            .data(callbackQueryService.buildCallbackData(
                                                    Command.SHOW_SPAM_BANNED_USER, spamBan.getPid().toString())
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
        antiSpam.removeFromCaptchaCash(chatId);
        start.run(chatId);
    }

    private boolean isEnteredCaptchaIsRight(Update update) {
        String text;
        if (updateService.hasMessageText(update)) {
            text = updateService.getMessageText(update);
        } else {
            text = callbackQueryService.getSplitData(update, 1);
        }
        return text.equals(antiSpam.getFromCaptchaCash(updateService.getChatId(update)));
    }

    public void send(Long chatId) {
        captchaSender.sendCaptcha(chatId);
        modifyUserService.nextStep(chatId, Command.CAPTCHA.name());
    }
}
