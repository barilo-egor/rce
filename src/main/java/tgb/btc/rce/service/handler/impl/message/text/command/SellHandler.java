package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.impl.state.DealHandler;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class SellHandler implements ITextCommandHandler {

    private final IModifyDealService modifyDealService;

    private final DealHandler dealHandler;

    public SellHandler(IModifyDealService modifyDealService, DealHandler dealHandler) {
        this.modifyDealService = modifyDealService;
        this.dealHandler = dealHandler;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();

        modifyDealService.createNewDeal(DealType.SELL, chatId);
        Update update = new Update();
        update.setMessage(message);
        dealHandler.handle(update);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.SELL_BITCOIN;
    }
}
