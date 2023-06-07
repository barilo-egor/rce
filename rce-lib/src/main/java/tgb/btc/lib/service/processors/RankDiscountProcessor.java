package tgb.btc.lib.service.processors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.UserDiscountRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;
import tgb.btc.lib.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.RANK_DISCOUNT)
public class RankDiscountProcessor extends Processor {

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Введите chat id пользователя для включения/выключения реферальной скидки",
                        KeyboardUtil.buildReply(List.of(
                                ReplyButton.builder()
                                        .text(Command.CANCEL.getText())
                                        .build())));
                userService.nextStep(chatId, Command.RANK_DISCOUNT);
                break;
            case 1:
                if (UpdateUtil.hasMessageText(update) && Command.CANCEL.getText().equals(UpdateUtil.getMessageText(update))) {
                    processToAdminMainPanel(chatId);
                    return;
                }
                sendUserRankDiscount(chatId, UpdateUtil.getLongFromText(update));
                processToAdminMainPanel(chatId);
                break;
        }
    }

    public void sendUserRankDiscount(Long chatId, Long userChatId) {
        if (!userService.existByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        boolean isRankDiscountOn = BooleanUtils.isTrue(userDiscountRepository.getRankDiscountByUserChatId(userChatId));
        responseSender.sendMessage(chatId, "Пользователь chat id=" + userChatId + ".",
                KeyboardUtil.buildInline(List.of(InlineButton.builder()
                        .text(isRankDiscountOn ? "Выключить" : "Включить")
                        .data(Command.CHANGE_RANK_DISCOUNT.getText()
                                + BotStringConstants.CALLBACK_DATA_SPLITTER + userChatId
                                + BotStringConstants.CALLBACK_DATA_SPLITTER + (!isRankDiscountOn))
                        .build())));
    }
}
