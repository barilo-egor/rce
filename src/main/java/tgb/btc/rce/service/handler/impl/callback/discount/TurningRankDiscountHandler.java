package tgb.btc.rce.service.handler.impl.callback.discount;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class TurningRankDiscountHandler implements ICallbackQueryHandler {

    private final VariablePropertiesReader variablePropertiesReader;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public TurningRankDiscountHandler(VariablePropertiesReader variablePropertiesReader, IResponseSender responseSender,
                                      ICallbackDataService callbackDataService) {
        this.variablePropertiesReader = variablePropertiesReader;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        boolean newValue = callbackDataService.getBoolArgument(callbackQuery.getData(), 1);
        variablePropertiesReader.setProperty(VariableType.DEAL_RANK_DISCOUNT_ENABLE.getKey(), newValue);
        responseSender.sendMessage(chatId, newValue ? "Скидка включена." : "Скидка выключена.");
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        variablePropertiesReader.reload();
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.TURNING_RANK_DISCOUNT;
    }
}
