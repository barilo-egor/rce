package tgb.btc.lib.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.PaymentReceipt;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.ReceiptFormat;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.DealService;
import tgb.btc.lib.service.processors.support.DealSupportService;
import tgb.btc.lib.util.UpdateUtil;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.NEW_DEALS)
public class NewDeals extends Processor {

    private DealService dealService;

    private DealSupportService dealSupportService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setDealSupportService(DealSupportService dealSupportService) {
        this.dealSupportService = dealSupportService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<Long> activeDeals = dealService.getActiveDealPids();

        if (activeDeals.isEmpty()) {
            responseSender.sendMessage(chatId, "Новых заявок нет.");
            return;
        }

        activeDeals.forEach(dealPid -> {
            responseSender.sendMessage(chatId, dealSupportService.dealToString(dealPid),
                    dealSupportService.dealToStringButtons(dealPid));
            List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(dealPid);
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
    }
}
