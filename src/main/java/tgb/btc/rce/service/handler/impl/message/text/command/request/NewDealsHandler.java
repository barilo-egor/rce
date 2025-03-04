package tgb.btc.rce.service.handler.impl.message.text.command.request;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.processors.support.DealSupportService;

import java.util.List;

@Service
public class NewDealsHandler implements ITextCommandHandler {

    private final IReadDealService readDealService;

    private final IResponseSender responseSender;

    private final IReadUserService readUserService;

    private final DealSupportService dealSupportService;

    public NewDealsHandler(IReadDealService readDealService, IResponseSender responseSender,
                           IReadUserService readUserService, DealSupportService dealSupportService) {
        this.readDealService = readDealService;
        this.responseSender = responseSender;
        this.readUserService = readUserService;
        this.dealSupportService = dealSupportService;
    }


    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        List<Long> activeDeals = readDealService.getPaidDealsPids();

        if (activeDeals.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }
        UserRole userRole = readUserService.getUserRoleByChatId(chatId);

        if (UserRole.OPERATOR_ACCESS.contains(userRole)) {
            activeDeals.forEach(dealPid -> {
                responseSender.sendMessage(chatId, dealSupportService.dealToString(dealPid),
                        dealSupportService.dealToStringButtons(dealPid));
                List<PaymentReceipt> paymentReceipts = readDealService.getPaymentReceipts(dealPid);
                paymentReceipts.forEach(paymentReceipt -> sendPaymentReceipt(paymentReceipt, chatId));
            });
        } else {
            activeDeals.forEach(dealPid -> responseSender.sendMessage(chatId, dealSupportService.dealToString(dealPid)));
        }
    }

    private void sendPaymentReceipt(PaymentReceipt paymentReceipt, Long chatId) {
        if (paymentReceipt.getReceiptFormat().equals(ReceiptFormat.PICTURE)) {
            responseSender.sendPhoto(chatId, StringUtils.EMPTY, paymentReceipt.getReceipt());
        } else if (paymentReceipt.getReceiptFormat().equals(ReceiptFormat.PDF)) {
            responseSender.sendFile(chatId, new InputFile(paymentReceipt.getReceipt()));
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.NEW_DEALS;
    }
}
