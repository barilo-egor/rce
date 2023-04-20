package tgb.btc.rce.service.processors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.RANK_DISCOUNT)
public class RankDiscountProcessor extends Processor {

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public RankDiscountProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

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
                if (Command.CANCEL.getText().equals(UpdateUtil.getMessageText(update))) {
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
