package tgb.btc.rce.service.processors.admin.settings.paymenttypes.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.math.BigDecimal;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE, step = 3)
public class CreateNewPaymentType extends Processor {

    private IPaymentTypeService paymentTypeService;

    private IUserDataService userDataService;

    private IFiatCurrencyService fiatCurrencyService;

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        String message = updateService.getMessageText(update);
        DealType dealType;
        FiatCurrency fiatCurrency;
        if (DealType.BUY.getNominativeFirstLetterToUpper().equals(message)) dealType = DealType.BUY;
        else if (DealType.SELL.getNominativeFirstLetterToUpper().equals(message)) dealType = DealType.SELL;
        else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL);
            return;
        }
        if (fiatCurrencyService.isFew()) {
            fiatCurrency = userDataService.getFiatCurrencyByChatId(chatId);
        } else {
            fiatCurrency = fiatCurrencyService.getFirst();
        }
        PaymentType paymentType = new PaymentType();
        paymentType.setName(userDataService.getStringByUserChatId(chatId));
        paymentType.setDealType(dealType);
        paymentType.setMinSum(BigDecimal.ZERO);
        paymentType.setFiatCurrency(fiatCurrency);
        paymentTypeService.save(paymentType);
        responseSender.sendMessage(chatId, "Новый тип оплаты сохранен. " +
                "Не забудьте установить минимальную сумму, добавить реквизиты и включить по необходимости.");
        modifyUserService.setDefaultValues(chatId);
        processToAdminMainPanel(chatId);
    }

}
