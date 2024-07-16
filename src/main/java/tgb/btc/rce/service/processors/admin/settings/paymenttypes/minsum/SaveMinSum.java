package tgb.btc.rce.service.processors.admin.settings.paymenttypes.minsum;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.math.BigDecimal;

@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 4)
public class SaveMinSum extends Processor {

    private IPaymentTypeService paymentTypeService;

    private IUserDataService userDataService;

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
        long minSum;
        try {
            minSum = Long.parseLong(updateService.getMessageText(update));
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка. Введите новую минимальную сумму.");
            return;
        }
        paymentTypeService.updateMinSumByPid(BigDecimal.valueOf(minSum),
                userDataService.getLongByUserPid(readUserService.getPidByChatId(chatId)));
        responseSender.sendMessage(chatId, "Минимальная сумма обновлена.");
        processToAdminMainPanel(chatId);
    }
}
