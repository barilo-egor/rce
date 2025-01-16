package tgb.btc.rce.service.handler.impl.callback.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.library.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class DeleteAndBlockUserHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IModifyDealService modifyDealService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final IResponseSender responseSender;

    private final IDealUserService dealUserService;

    private final IDealPropertyService dealPropertyService;

    private final IModifyUserService modifyUserService;

    private final String botUsername;

    public DeleteAndBlockUserHandler(ICallbackDataService callbackDataService, IModifyDealService modifyDealService,
                                     ICryptoWithdrawalService cryptoWithdrawalService, IResponseSender responseSender,
                                     IDealUserService dealUserService,
                                     IDealPropertyService dealPropertyService, IModifyUserService modifyUserService,
                                     @Value("${bot.username}") String botUsername) {
        this.callbackDataService = callbackDataService;
        this.modifyDealService = modifyDealService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.responseSender = responseSender;
        this.dealUserService = dealUserService;
        this.dealPropertyService = dealPropertyService;
        this.modifyUserService = modifyUserService;
        this.botUsername = botUsername;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        DealStatus dealStatus = dealPropertyService.getDealStatusByPid(dealPid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        if (DealStatus.CONFIRMED.equals(dealStatus)) {
            responseSender.sendMessage(chatId, "Заявка уже подтверждена, удаление невозможно.");
            return;
        }
        Long userChatId = dealUserService.getUserChatIdByDealPid(dealPid);
        log.info("Админ {} удалил заявку {} пользователя {} и заблокировал его.", chatId, dealPid, userChatId);
        modifyDealService.deleteDeal(dealPid, true);
        modifyUserService.updateCurrentDealByChatId(null, userChatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETE_DEAL_AND_BLOCK_USER;
    }
}
