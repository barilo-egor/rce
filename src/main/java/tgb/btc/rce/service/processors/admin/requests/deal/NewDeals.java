package tgb.btc.rce.service.processors.admin.requests.deal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.DealSupportService;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.NEW_DEALS)
public class NewDeals extends Processor {

    private IReadDealService readDealService;

    private DealSupportService dealSupportService;

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
        Long chatId = updateService.getChatId(update);
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
                if (paymentReceipts.size() > 0) {
                    List<InputMedia> inputMedia = new ArrayList<>();
                    for (PaymentReceipt paymentReceipt : paymentReceipts) {
                        if (paymentReceipt.getReceiptFormat().equals(ReceiptFormat.PICTURE)) {
                            responseSender.sendPhoto(chatId, StringUtils.EMPTY, paymentReceipt.getReceipt());
                        } else if (paymentReceipt.getReceiptFormat().equals(ReceiptFormat.PDF)) {
                            responseSender.sendInputFile(chatId, new InputFile(paymentReceipt.getReceipt()));
                        }
                    }
                    responseSender.sendMedia(chatId, inputMedia);
                }
            });
        } else {
            activeDeals.forEach(dealPid -> {
                responseSender.sendMessage(chatId, dealSupportService.dealToString(dealPid));
            });
        }
    }
}
