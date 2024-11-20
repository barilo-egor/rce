package tgb.btc.rce.service.handler.impl.callback.deal.pool;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class BitcoinPoolWithdrawalHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    private final IResponseSender responseSender;

    public BitcoinPoolWithdrawalHandler(ICallbackDataService callbackDataService, IKeyboardBuildService keyboardBuildService,
                                        IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealsSize = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        String totalAmount = callbackDataService.getArgument(callbackQuery.getData(), 2);
        ReplyKeyboard replyKeyboard = keyboardBuildService.buildInline(
                List.of(
                        InlineButton.builder()
                                .text("Да")
                                .data(callbackDataService.buildData(
                                        CallbackQueryData.CONFIRM_POOL_WITHDRAWAL,
                                        callbackQuery.getMessage().getMessageId()
                                ))
                                .build(),
                        InlineButton.builder().text("Нет").data(CallbackQueryData.INLINE_DELETE.name()).build()
                ),
                2
        );
        responseSender.sendMessage(chatId, "Вы собираетесь подтвердить и вывести все <b>" + dealsSize
                + "</b> сделок из пула на общую сумму <b>" + totalAmount + "</b> . Продолжить?", replyKeyboard, "html");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.BITCOIN_POOL_WITHDRAWAL;
    }
}
