package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.MessageTemplate;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.USER_REFERRAL_BALANCE)
public class UserReferralBalance extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) {
            processToAdminMainPanel(chatId);
            return;
        }
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, MessageTemplate.ASK_CHAT_ID);
                userRepository.nextStep(chatId, Command.USER_REFERRAL_BALANCE.name());
                break;
            case 1:
                Long userChatId = Long.parseLong(update.getMessage().getText());
                if (userService.existByChatId(userChatId)) {
                    responseSender.sendMessage(chatId, "У пользователя с чат айди " + userChatId
                            + " на реферальном балансе " + userService.getReferralBalanceByChatId(userChatId) + "₽",
                            KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .inlineType(InlineType.CALLBACK_DATA)
                                            .text("Изменить")
                                            .data(Command.CHANGE_REFERRAL_BALANCE.getText()
                                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + userChatId)
                                            .build()
                            )));
                    processToAdminMainPanel(chatId);
                } else responseSender.sendMessage(chatId, "Пользователь не найден.");
        }
    }
}
