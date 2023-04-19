package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReceiptFormat;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

import java.time.LocalDate;
import java.util.List;

@CommandProcessor(command = Command.SEND_CHECKS_FOR_DATE)
public class SendChecksForDate extends Processor {

    private DealRepository dealRepository;

    private DealService dealService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public SendChecksForDate(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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

        List<Deal> deals = dealRepository.getByDate(date);
        for (Deal deal : deals) {
            List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(deal.getPid());
            paymentReceipts.forEach(paymentReceipt -> {
                if (ReceiptFormat.PDF.equals(paymentReceipt.getReceiptFormat())) {
                    responseSender.sendInputFile(chatId, new InputFile(paymentReceipt.getReceipt()));
                } else if (ReceiptFormat.PICTURE.equals(paymentReceipt.getReceiptFormat())) {
                    responseSender.sendPhoto(chatId, "№" + deal.getPid(), deal.getUserCheck());
                }
            });
        }
        processToAdminMainPanel(chatId);
    }

}
