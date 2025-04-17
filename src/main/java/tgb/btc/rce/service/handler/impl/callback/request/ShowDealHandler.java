package tgb.btc.rce.service.handler.impl.callback.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Objects;

@Service
public class ShowDealHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IReadDealService readDealService;

    private final IReadUserService readUserService;

    private final DealSupportService dealSupportService;

    public ShowDealHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                           IReadDealService readDealService, IReadUserService readUserService,
                           DealSupportService dealSupportService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.readDealService = readDealService;
        this.readUserService = readUserService;
        this.dealSupportService = dealSupportService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        try {
            responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        } catch (Exception ignored) {
        }
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Deal deal = readDealService.findByPid(dealPid);
        if (Objects.isNull(deal)) {
            responseSender.sendMessage(chatId, "Заявка была удалена.");
            return;
        }
        UserRole userRole = readUserService.getUserRoleByChatId(chatId);
        dealSupportService.sendDeal(chatId, userRole, deal);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SHOW_DEAL;
    }
}
