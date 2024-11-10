package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.tool.Start;


@CommandProcessor(command = Command.DELETE_DEAL_AND_BLOCK_USER)
@Slf4j
public class DeleteDealAndBlockUserProcessor extends Processor {

    private IDealUserService dealUserService;

    private Start start;

    private IModifyDealService modifyDealService;

    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Value("${bot.username}")
    private String botUsername;

    @Autowired
    public void setCryptoWithdrawalService(ICryptoWithdrawalService cryptoWithdrawalService) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
    }

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Autowired
    public void setDealUserService(IDealUserService dealUserService) {
        this.dealUserService = dealUserService;
    }

    @Autowired
    public void setStart(Start start) {
        this.start = start;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = updateService.getChatId(update);
        Long dealPid = Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        Long userChatId = dealUserService.getUserChatIdByDealPid(dealPid);
        log.info("Админ " + chatId + " удалил заявку " + dealPid + " пользователя " + userChatId + " и заблокировал его.");
        modifyDealService.deleteDeal(dealPid, true);
        new Thread(() -> cryptoWithdrawalService.deleteFromPool(botUsername, dealPid)).start();
        responseSender.sendMessage(chatId, "Заявка №" + dealPid + " удалена.");
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        start.run(userChatId);
    }
}
