package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.library.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ITelegramPropertiesService;


@CommandProcessor(command = Command.DELETE_USER_DEAL)
@Slf4j
public class DeleteUserDeal extends Processor {

    private IDealUserService dealUserService;

    private IModifyDealService modifyDealService;

    private ICryptoWithdrawalService cryptoWithdrawalService;

    private String botUsername;

    public DeleteUserDeal(IDealUserService dealUserService, IModifyDealService modifyDealService,
                          ICryptoWithdrawalService cryptoWithdrawalService,
                          ITelegramPropertiesService telegramPropertiesService) {
        this.dealUserService = dealUserService;
        this.modifyDealService = modifyDealService;
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        botUsername = telegramPropertiesService.getUsername();
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = updateService.getChatId(update);
        Long dealPid = Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        Long userChatId = dealUserService.getUserChatIdByDealPid(dealPid);
        modifyDealService.deleteById(dealPid);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        log.info("Админ " + chatId + " удалил сделку " + dealPid + " пользователя " + userChatId);
        modifyUserService.updateCurrentDealByChatId(null, userChatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(userChatId, messagePropertiesService.getMessage("deal.deleted.by.admin"));
    }
}
