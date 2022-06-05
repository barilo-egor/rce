package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;

@CommandProcessor(command = Command.SEND_LINK)
public class SendLink extends Processor {
    public SendLink(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

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
