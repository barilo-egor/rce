package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReceiptFormat;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.util.UpdateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CommandProcessor(command = Command.SHOW_DEAL)
public class ShowDeal extends Processor {

    private final DealSupportService dealSupportService;
    private final DealService dealService;

    @Autowired
    public ShowDeal(IResponseSender responseSender, UserService userService, DealSupportService dealSupportService, DealService dealService) {
        super(responseSender, userService);
        this.dealSupportService = dealSupportService;
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
    }
}
