package tgb.btc.rce.service.processors.admin.users;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.USER_REFERRAL_BALANCE)
public class UserReferralBalance extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (checkForCancel(update)) {
            processToAdminMainPanel(chatId);
            return;
        }
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Введите чат айди пользователя.", keyboardService.getReplyCancel());
                modifyUserService.nextStep(chatId, Command.USER_REFERRAL_BALANCE.name());
                break;
            case 1:
                Long userChatId = Long.parseLong(update.getMessage().getText());
                if (readUserService.existsByChatId(userChatId)) {
                    responseSender.sendMessage(chatId, "У пользователя с чат айди " + userChatId
                            + " на реферальном балансе " + readUserService.getReferralBalanceByChatId(userChatId) + "₽",
                            keyboardBuildService.buildInline(List.of(
                                    InlineButton.builder()
                                            .inlineType(InlineType.CALLBACK_DATA)
                                            .text("Изменить")
                                            .data(Command.CHANGE_REFERRAL_BALANCE.name()
                                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + userChatId)
                                            .build()
                            )));
                    processToAdminMainPanel(chatId);
                } else responseSender.sendMessage(chatId, "Пользователь не найден.");
        }
    }
}
