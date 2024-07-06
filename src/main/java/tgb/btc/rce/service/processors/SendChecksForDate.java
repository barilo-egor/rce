package tgb.btc.rce.service.processors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDateDealService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.time.LocalDate;
import java.util.List;

@CommandProcessor(command = Command.SEND_CHECKS_FOR_DATE)
public class SendChecksForDate extends Processor {

    private IReadDealService readDealService;

    private IDateDealService dateDealService;

    @Autowired
    public void setDateDealService(IDateDealService dateDealService) {
        this.dateDealService = dateDealService;
    }

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String[] enteredValues = UpdateUtil.getMessageText(update).split("\\.");
        int day;
        int month;
        int year;
        LocalDate date;
        try {
            day = Integer.parseInt(enteredValues[0]);
            month = Integer.parseInt(enteredValues[1]);
            year = Integer.parseInt(enteredValues[2]);
            date = LocalDate.of(year, month, day);
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Неверный формат.");
            return;
        }

        List<Deal> deals = dateDealService.getPassedByDate(date);

        if (CollectionUtils.isEmpty(deals)) {
            responseSender.sendMessage(chatId, "Сделки за дату отсутствуют.");
        } else {
            for (Deal deal : deals) {
                List<PaymentReceipt> paymentReceipts = readDealService.getPaymentReceipts(deal.getPid());
                paymentReceipts.forEach(paymentReceipt -> {
                    if (ReceiptFormat.PDF.equals(paymentReceipt.getReceiptFormat())) {
                        responseSender.sendInputFile(chatId, new InputFile(paymentReceipt.getReceipt()));
                    } else if (ReceiptFormat.PICTURE.equals(paymentReceipt.getReceiptFormat())) {
                        responseSender.sendPhoto(chatId, "№" + deal.getPid(), paymentReceipt.getReceipt());
                    }
                });
            }
        }
        processToAdminMainPanel(chatId);
    }

}
