package tgb.btc.rce.service.handler.impl.state;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDateDealService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;

import java.time.LocalDate;
import java.util.List;

@Service
public class CheckForDateStateHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IAdminPanelService adminPanelService;

    private final IRedisUserStateService redisUserStateService;

    private final IDateDealService dateDealService;

    private final IReadDealService readDealService;

    public CheckForDateStateHandler(IResponseSender responseSender, IAdminPanelService adminPanelService,
                                    IRedisUserStateService redisUserStateService, IDateDealService dateDealService,
                                    IReadDealService readDealService) {
        this.responseSender = responseSender;
        this.adminPanelService = adminPanelService;
        this.redisUserStateService = redisUserStateService;
        this.dateDealService = dateDealService;
        this.readDealService = readDealService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите дату или нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        String[] enteredValues = text.split("\\.");
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
            responseSender.sendMessage(chatId, "Ошибка при попытке парса даты. Проверьте валидность введенной даты.");
            return;
        }

        List<Deal> deals = dateDealService.getConfirmedByDateBetween(date);

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
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.CHECK_FOR_DATE;
    }
}
