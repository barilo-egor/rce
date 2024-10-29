package tgb.btc.rce.service.processors.admin.requests.deal;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.BITCOIN_POOL_WITHDRAWAL)
public class BitcoinPoolWithdrawal extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long dealsSize = callbackQueryService.getSplitLongData(update, 1);
        String totalAmount = callbackQueryService.getSplitData(update, 2);
        ReplyKeyboard replyKeyboard = keyboardBuildService.buildInline(
                List.of(
                        InlineButton.builder()
                                .text(commandService.getText(Command.CONFIRM_POOL_WITHDRAWAL))
                                .data(callbackQueryService.buildCallbackData(
                                        Command.CONFIRM_POOL_WITHDRAWAL,
                                        new Object[]{updateService.getMessageId(update)}
                                        ))
                                .build(),
                        InlineButton.builder().text("Нет").data(Command.INLINE_DELETE.name()).build()
                ),
                2
        );
        responseSender.sendMessage(chatId, "Вы собираетесь подтвердить и вывести все <b>" + dealsSize
                + "</b> сделок из пула на общую сумму <b>" + totalAmount + "</b> . Продолжить?", replyKeyboard, "html");
    }
}
