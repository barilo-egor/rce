package tgb.btc.rce.service.handler.impl.callback.deal;

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
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

@Service
@Slf4j
public class DeleteUserDealHandler implements ICallbackQueryHandler {

    private final IDealUserService dealUserService;

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final ICallbackDataService callbackDataService;

    private final IModifyDealService modifyDealService;

    private final IModifyUserService modifyUserService;

    private final IResponseSender responseSender;

    private final IMessagePropertiesService messagePropertiesService;

    private final IDealPropertyService dealPropertyService;

    @Value("${bot.username}")
    private String botUsername;

    public DeleteUserDealHandler(IDealUserService dealUserService, ICryptoWithdrawalService cryptoWithdrawalService,
                                 ICallbackDataService callbackDataService, IModifyDealService modifyDealService,
                                 IModifyUserService modifyUserService, IResponseSender responseSender,
                                 IMessagePropertiesService messagePropertiesService,
                                 IDealPropertyService dealPropertyService) {
        this.dealUserService = dealUserService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.callbackDataService = callbackDataService;
        this.modifyDealService = modifyDealService;
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
        this.messagePropertiesService = messagePropertiesService;
        this.dealPropertyService = dealPropertyService;
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
        modifyDealService.deleteById(dealPid);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        log.info("Админ {} удалил сделку {} пользователя {}", chatId, dealPid, userChatId);
        modifyUserService.updateCurrentDealByChatId(null, userChatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
        responseSender.sendMessage(userChatId, messagePropertiesService.getMessage("deal.deleted.by.admin"));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETE_USER_DEAL;
    }
}
