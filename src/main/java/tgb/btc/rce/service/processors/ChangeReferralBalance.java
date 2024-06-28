package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.CHANGE_REFERRAL_BALANCE)
@Slf4j
public class ChangeReferralBalance extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) {
            processToAdminMainPanel(chatId);
            return;
        }
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Введите новую сумму для пользователя.\n" +
                                "Для того, чтобы полностью заменить значение, отправьте новое число без знаков. Пример:\n1750\n\n" +
                                "Чтобы добавить к текущему значению баланса сумму, отправьте число со знаком \"+\". Пример:\n+1750\n\n" +
                                "Чтобы отнять от текущего значения баланса сумму, отправьте число со знаком \"-\". Пример:\n-1750",
                        KeyboardUtil.buildReply(List.of(
                                ReplyButton.builder()
                                        .text(Command.CANCEL.getText())
                                        .build())));
                modifyUserService.updateBufferVariable(chatId,
                        update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
                userRepository.nextStep(chatId, Command.CHANGE_REFERRAL_BALANCE.name());
                break;
            case 1:
                if (!update.hasMessage() || !update.getMessage().hasText()) throw new BaseException("Не найден текст.");
                String text = UpdateUtil.getMessageText(update);
                Long userChatId = Long.parseLong(readUserService.getBufferVariable(chatId));
                if (text.startsWith("+")) {
                    Integer userReferralBalance = readUserService.getReferralBalanceByChatId(userChatId);
                    Integer enteredSum = Integer.parseInt(text.substring(1));
                    Integer total = userReferralBalance + enteredSum;
                    log.info("Админ с чат айди " + chatId + " добавил на баланс пользователю с чат айди " + userChatId
                            + " - " + enteredSum + " рублей. enteredSum = " + enteredSum + "; userReferralBalance = " + userReferralBalance + "; total = " + total);
                    modifyUserService.updateReferralBalanceByChatId(total, userChatId);
                    responseSender.sendMessage(userChatId, "На ваш реферальный баланс было зачислено " + Integer.parseInt(text.substring(1)) + "₽.");
                }
                else if (text.startsWith("-")) {
                    Integer userReferralBalance = readUserService.getReferralBalanceByChatId(userChatId);
                    Integer enteredSum = Integer.parseInt(text.substring(1));
                    int total = userReferralBalance - enteredSum;
                    log.info("Админ с чат айди " + chatId + " убрал с баланса пользователю с чат айди " + userChatId
                            + " - " + enteredSum + " рублей. enteredSum = " + enteredSum + "; userReferralBalance = " + userReferralBalance + "; total = " + total);
                    modifyUserService.updateReferralBalanceByChatId(
                            userReferralBalance
                                    - enteredSum, userChatId);
                    responseSender.sendMessage(userChatId, "С вашего реферального баланса списано " + Integer.parseInt(text.substring(1)) + "₽.");
                } else {
                    log.info("Админ с чат айди " + chatId + " засетал баланс пользователю с чат айди " + userChatId
                            + " - " + Integer.parseInt(text) + " рублей");
                    modifyUserService.updateReferralBalanceByChatId(Integer.parseInt(text), userChatId);
                }
                responseSender.sendMessage(chatId, "Баланс изменен.");
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
