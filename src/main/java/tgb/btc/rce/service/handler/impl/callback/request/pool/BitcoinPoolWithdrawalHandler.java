package tgb.btc.rce.service.handler.impl.callback.request.pool;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class BitcoinPoolWithdrawalHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    private final IResponseSender responseSender;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    public BitcoinPoolWithdrawalHandler(ICallbackDataService callbackDataService, IKeyboardBuildService keyboardBuildService,
                                        IResponseSender responseSender, ICryptoWithdrawalService cryptoWithdrawalService) {
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
        this.responseSender = responseSender;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealsSize = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        String totalAmount = callbackDataService.getArgument(callbackQuery.getData(), 2);
        sendConfirmMessage(chatId, dealsSize, totalAmount, callbackQuery.getMessage().getMessageId());
    }

    public void sendConfirmMessage(Long chatId, Long dealsSize, String totalAmount, Integer messageId) {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text("Да")
                .data(callbackDataService.buildData(
                        CallbackQueryData.CONFIRM_POOL_WITHDRAWAL,
                        messageId
                ))
                .build());
        buttons.add(InlineButton.builder().text("Нет").data(CallbackQueryData.INLINE_DELETE.name()).build());
        buttons.add(InlineButton.builder()
                .text("Изменить комиссию")
                .data(callbackDataService.buildData(CallbackQueryData.POOL_CHANGE_FEE_RATE, dealsSize, totalAmount, messageId))
                .build());
        ReplyKeyboard replyKeyboard = keyboardBuildService.buildInline(buttons, 2);
        responseSender.sendMessage(chatId, getMessage(dealsSize, totalAmount), replyKeyboard);
    }

    private String getMessage(Long dealsSize, String totalAmount) {
        StringBuilder message = new StringBuilder();
        String feeRate = cryptoWithdrawalService.getFeeRate(CryptoCurrency.BITCOIN);
        if (cryptoWithdrawalService.isAutoFeeRate(CryptoCurrency.BITCOIN)) {
            message.append("Комиссия: <b>").append(feeRate).append("</b>\n");
        } else {
            message.append("Комиссия: <b>").append(feeRate).append(" sat/vB</b>\n");
        }
        message.append("Вы собираетесь подтвердить и вывести все <b>").append(dealsSize)
                .append("</b> сделок из пула на общую сумму <b>").append(totalAmount)
                .append("</b> . Продолжить?");
        return message.toString();
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.BITCOIN_POOL_WITHDRAWAL;
    }
}
