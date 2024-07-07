package tgb.btc.rce.service.processors.admin.settings.disounts;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.RANK_DISCOUNT)
public class RankDiscountProcessor extends Processor {

    private IUserDiscountService userDiscountService;

    @Autowired
    public void setUserDiscountService(IUserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Введите chat id пользователя для включения/выключения реферальной скидки",
                        KeyboardUtil.buildReply(List.of(
                                ReplyButton.builder()
                                        .text(Command.CANCEL.getText())
                                        .build())));
                modifyUserService.nextStep(chatId, Command.RANK_DISCOUNT.name());
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
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        boolean isRankDiscountOn = BooleanUtils.isTrue(userDiscountService.getRankDiscountByUserChatId(userChatId));
        responseSender.sendMessage(chatId, "Пользователь chat id=" + userChatId + ".",
                KeyboardUtil.buildInline(List.of(InlineButton.builder()
                        .text(isRankDiscountOn ? "Выключить" : "Включить")
                        .data(Command.CHANGE_RANK_DISCOUNT.getText()
                                + BotStringConstants.CALLBACK_DATA_SPLITTER + userChatId
                                + BotStringConstants.CALLBACK_DATA_SPLITTER + (!isRankDiscountOn))
                        .build())));
    }
}
