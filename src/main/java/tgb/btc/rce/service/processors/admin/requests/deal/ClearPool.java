package tgb.btc.rce.service.processors.admin.requests.deal;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.CLEAR_POOL)
public class ClearPool extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long dealsSize = callbackQueryService.getSplitLongData(update, 1);
        ReplyKeyboard replyKeyboard = keyboardBuildService.buildInline(List.of(
                InlineButton.builder().text(commandService.getText(Command.CONFIRM_CLEAR_POOL))
                        .data(callbackQueryService.buildCallbackData(Command.CONFIRM_CLEAR_POOL, updateService.getMessageId(update))).build(),
                InlineButton.builder().text("Нет").data(Command.INLINE_DELETE.name()).build()
        ), 2);
        responseSender.sendMessage(chatId, "Вы собираетесь удалить все <b>" + dealsSize + "</b> сделок из BTC пула. Продолжить?", replyKeyboard, "html");
    }
}
