package tgb.btc.lib.service.processors;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;

@CommandProcessor(command = Command.SEND_LINK)
public class SendLink extends Processor {

    @Override
    public void run(Update update) {
        responseSender.execute(AnswerInlineQuery.builder().inlineQueryId(update.getInlineQuery().getId())
                .result(InlineQueryResultArticle.builder()
                        .id(update.getInlineQuery().getId())
                        .title("Отправка реферальной ссылки другу.")
                        .inputMessageContent(InputTextMessageContent.builder()
                                .messageText(update.getInlineQuery().getQuery())
                                .build())
                        .description("Нажмите сюда, чтобы отправить ссылку другу.")
                        .build())
                .build());
    }
}
