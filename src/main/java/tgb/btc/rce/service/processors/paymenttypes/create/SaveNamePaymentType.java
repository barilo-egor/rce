package tgb.btc.rce.service.processors.paymenttypes.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.PAYMENT_TYPES, step = 1)
public class SaveNamePaymentType extends Processor {

    public static final String BUY_OR_SELL = "Покупка или продажа?";
    public static final String BUY = "Покупка";
    public static final String SELL = "Продажа";

    @Autowired
    public SaveNamePaymentType(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!hasMessageText(update, NewPaymentType.ENTER_NAME)) return;
        String newTypeName = UpdateUtil.getMessageText(update);
        userService.updateBufferVariable(chatId, newTypeName);
        responseSender.sendMessage(chatId, BUY_OR_SELL,
                                   KeyboardUtil.buildReply(List.of(
                                           ReplyButton.builder()
                                                   .text(BUY)
                                                   .build(),
                                           ReplyButton.builder()
                                                   .text(SELL)
                                                   .build(),
                                           KeyboardUtil.getCancelButton()
                                   )));
        userService.nextStep(chatId);
    }

}
