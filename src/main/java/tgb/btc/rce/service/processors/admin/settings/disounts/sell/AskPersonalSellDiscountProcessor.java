package tgb.btc.rce.service.processors.admin.settings.disounts.sell;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@CommandProcessor(command = Command.PERSONAL_SELL_DISCOUNT, step = 1)
public class AskPersonalSellDiscountProcessor extends Processor {

    private IUserDiscountService userDiscountService;

    @Autowired
    public void setUserDiscountService(IUserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Override
    public void run(Update update) {
        Long userChatId = UpdateUtil.getLongFromText(update);
        Long chatId = UpdateUtil.getChatId(update);
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        BigDecimal personalSell = userDiscountService.getPersonalSellByChatId(userChatId);
        modifyUserService.updateBufferVariable(chatId, userChatId.toString());
        if (Objects.isNull(personalSell)) personalSell = BigDecimal.ZERO;

        responseSender.sendMessage(chatId, "У пользователя " + userChatId + " текущее значение скидки на продажу = "
                + personalSell.stripTrailingZeros().toPlainString());
        responseSender.sendMessage(chatId, "Введите положительное значение для скидки, либо отрицательное для надбавки.");
        modifyUserService.nextStep(chatId);
    }

}
