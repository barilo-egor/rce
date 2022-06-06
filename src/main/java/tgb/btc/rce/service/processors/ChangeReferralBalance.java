package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.CHANGE_REFERRAL_BALANCE)
public class ChangeReferralBalance extends Processor {

    @Autowired
    public ChangeReferralBalance(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) {
            processToAdminMainPanel(chatId);
            return;
        }
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Введите новую сумму для пользователя.\n" +
                                "Для того, чтобы полностью заменить значение, отправьте новое число без знаков. Пример:\n1750\n\n" +
                                "Чтобы добавить к текущему значению баланса сумму, отправьте число со знаком \"+\". Пример:\n+1750\n\n" +
                                "Чтобы отнять от текущего значения баланса сумму, отправьте число со знаком \"-\". Пример:\n-1750",
                        KeyboardUtil.buildReply(List.of(
                                ReplyButton.builder()
                                        .text(Command.CANCEL.getText())
                                        .build())));
                userService.updateBufferVariable(chatId,
                        update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
                userService.nextStep(chatId, Command.CHANGE_REFERRAL_BALANCE);
                break;
            case 1:
                if (!update.hasMessage() || !update.getMessage().hasText()) throw new BaseException("Не найден текст.");
                String text = UpdateUtil.getMessageText(update);
                Long userChatId = Long.parseLong(userService.getBufferVariable(chatId));
                if (text.startsWith("+")) userService.updateReferralBalanceByChatId(
                        userService.getReferralBalanceByChatId(userChatId)
                                + Integer.parseInt(text.substring(1)), userChatId);
                else if (text.startsWith("-")) userService.updateReferralBalanceByChatId(
                        userService.getReferralBalanceByChatId(userChatId)
                                - Integer.parseInt(text.substring(1)), userChatId);
                else
                    userService.updateReferralBalanceByChatId(Integer.parseInt(text), userChatId);
                responseSender.sendMessage(chatId, "Баланс изменен.");
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
