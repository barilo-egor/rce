package tgb.btc.lib.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.Deal;
import tgb.btc.lib.bean.PaymentReceipt;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.ReceiptFormat;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.DealService;
import tgb.btc.lib.service.processors.support.DealSupportService;
import tgb.btc.lib.util.UpdateUtil;

import java.util.List;
import java.util.Objects;

@CommandProcessor(command = Command.SHOW_DEAL)
public class ShowDeal extends Processor {

    private DealSupportService dealSupportService;
    private DealService dealService;

    @Autowired
    public void setDealSupportService(DealSupportService dealSupportService) {
        this.dealSupportService = dealSupportService;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.deleteMessage(chatId, UpdateUtil.getMessage(update).getMessageId());
        } catch (Exception ignored) {
        }
        Long dealPid = Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        Deal deal = dealService.getByPid(dealPid);
        if (Objects.isNull(deal)) {
            responseSender.sendMessage(chatId, "Заявка была удалена.");
            return;
        }
        String dealInfo = dealSupportService.dealToString(dealPid);
        responseSender.sendMessage(chatId, dealInfo, dealSupportService.dealToStringButtons(dealPid));
        List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(dealPid);
        if (paymentReceipts.size() > 0) {
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
