package tgb.btc.rce.service.handler.impl.callback.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

@Service
public class SaveDealRequisiteHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final IReadDealService readDealService;

    private final IModifyDealService modifyDealService;

    private final ICallbackDataService callbackDataService;

    public SaveDealRequisiteHandler(IResponseSender responseSender, IReadDealService readDealService,
                                    IModifyDealService modifyDealService, ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.readDealService = readDealService;
        this.modifyDealService = modifyDealService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();;
        String requisite = callbackQuery.getMessage().getText();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Integer messageId = callbackDataService.getIntArgument(callbackQuery.getData(), 2);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.deleteMessage(chatId, messageId);
        Deal deal = readDealService.findByPid(dealPid);
        deal.setWallet(requisite);
        modifyDealService.save(deal);
        responseSender.sendMessage(chatId, "Реквизит сделки №<b>" + dealPid + "</b> обновлен. " +
                "Предыдущее сообщение заявки удалено. Для отображения нажмите \"Показать\".",
                InlineButton.builder().text("Показать").data(callbackDataService.buildData(CallbackQueryData.SHOW_DEAL, dealPid)).build());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SAVE_DEAL_REQUISITE;
    }
}
