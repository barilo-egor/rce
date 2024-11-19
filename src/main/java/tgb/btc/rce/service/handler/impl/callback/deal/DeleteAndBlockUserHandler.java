package tgb.btc.rce.service.handler.impl.callback.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class DeleteAndBlockUserHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IModifyDealService modifyDealService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final IResponseSender responseSender;

    private final IStartService startService;

    private final IDealUserService dealUserService;

    @Value("${bot.username}")
    private String botUsername;

    public DeleteAndBlockUserHandler(ICallbackDataService callbackDataService, IModifyDealService modifyDealService,
                                     ICryptoWithdrawalService cryptoWithdrawalService, IResponseSender responseSender,
                                     IStartService startService, IDealUserService dealUserService) {
        this.callbackDataService = callbackDataService;
        this.modifyDealService = modifyDealService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.responseSender = responseSender;
        this.startService = startService;
        this.dealUserService = dealUserService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Long userChatId = dealUserService.getUserChatIdByDealPid(dealPid);
        log.info("Админ {} удалил заявку {} пользователя {} и заблокировал его.", chatId, dealPid, userChatId);
        modifyDealService.deleteDeal(dealPid, true);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        startService.process(userChatId);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETE_DEAL_AND_BLOCK_USER;
    }
}
