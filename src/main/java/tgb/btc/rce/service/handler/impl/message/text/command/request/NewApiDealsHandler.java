package tgb.btc.rce.service.handler.impl.message.text.command.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.process.IApiDealBotService;

import java.util.List;

@Service
public class NewApiDealsHandler implements ITextCommandHandler {

    private final IApiDealService apiDealService;

    private final IResponseSender responseSender;

    private final IApiDealBotService apiDealBotService;

    public NewApiDealsHandler(IApiDealService apiDealService, IResponseSender responseSender,
                              IApiDealBotService apiDealBotService) {
        this.apiDealService = apiDealService;
        this.responseSender = responseSender;
        this.apiDealBotService = apiDealBotService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        List<Long> activeDeals = apiDealService.getActiveDealsPids();

        if (activeDeals.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        activeDeals.forEach(pid -> apiDealBotService.sendApiDeal(pid, chatId));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.NEW_API_DEALS;
    }
}
