package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.processors.deal.DealProcessor;

@Service
public class SellHandler implements ITextCommandHandler {

    private final IModifyDealService modifyDealService;

    private final DealProcessor dealProcessor;

    public SellHandler(IModifyDealService modifyDealService, DealProcessor dealProcessor) {
        this.modifyDealService = modifyDealService;
        this.dealProcessor = dealProcessor;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();

        modifyDealService.createNewDeal(DealType.SELL, chatId);
        Update update = new Update();
        update.setMessage(message);
        dealProcessor.run(update);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.SELL_BITCOIN;
    }
}
