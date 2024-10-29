package tgb.btc.rce.service.processors.admin.requests.deal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.DealSupportService;

import java.util.List;
import java.util.Objects;

@CommandProcessor(command = Command.SHOW_DEAL)
public class ShowDeal extends Processor {

    private DealSupportService dealSupportService;

    private IReadDealService readDealService;

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Autowired
    public void setDealSupportService(DealSupportService dealSupportService) {
        this.dealSupportService = dealSupportService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = updateService.getChatId(update);
        try {
            responseSender.deleteMessage(chatId, updateService.getMessage(update).getMessageId());
        } catch (Exception ignored) {
        }
        Long dealPid = Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        Deal deal = readDealService.findByPid(dealPid);
        if (Objects.isNull(deal)) {
            responseSender.sendMessage(chatId, "Заявка была удалена.");
            return;
        }
        UserRole userRole = readUserService.getUserRoleByChatId(chatId);
        String dealInfo = dealSupportService.dealToString(dealPid);
        if (UserRole.OPERATOR_ACCESS.contains(userRole)) {
            responseSender.sendMessage(chatId, dealInfo, dealSupportService.dealToStringButtons(dealPid));
        } else {
            responseSender.sendMessage(chatId, dealInfo);
        }
        if (!UserRole.OPERATOR_ACCESS.contains(userRole)) {
            return;
        }
        List<PaymentReceipt> paymentReceipts = readDealService.getPaymentReceipts(dealPid);
        if (!paymentReceipts.isEmpty()) {
            for (PaymentReceipt paymentReceipt : paymentReceipts) {
                if (paymentReceipt.getReceiptFormat().equals(ReceiptFormat.PICTURE)) {
                    responseSender.sendPhoto(chatId, StringUtils.EMPTY, paymentReceipt.getReceipt());
                } else if (paymentReceipt.getReceiptFormat().equals(ReceiptFormat.PDF)) {
                    responseSender.sendInputFile(chatId, new InputFile(paymentReceipt.getReceipt()));
                }
            }
        }
    }
}
