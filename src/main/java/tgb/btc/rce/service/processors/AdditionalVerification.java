package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.ADDITIONAL_VERIFICATION)
public class AdditionalVerification extends Processor {

    private final DealService dealService;

    @Autowired
    public AdditionalVerification(IResponseSender responseSender, UserService userService, DealService dealService) {
        super(responseSender, userService);
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long dealPid = Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        Long userChatId = dealService.getUserByDealPid(dealPid);
        userService.nextStep(userChatId, Command.USER_ADDITIONAL_VERIFICATION);
        userService.updateBufferVariable(userChatId, dealPid.toString());
        responseSender.sendMessage(userChatId,
                "Необходимо пройти дополнительную верификацию. Предоставьте фото карты с которой была оплата, " +
                        "либо чека на фоне переписки с ботом для завершения сделки.",
                KeyboardUtil.buildReply(List.of(ReplyButton.builder().text("Отказаться от верификации").build())));
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Дополнительная верификация запрошена.");
    }
}