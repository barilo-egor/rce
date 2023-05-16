package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.UserData;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_USD_COURSE)
public class ChangeUsdCourseProcessor extends Processor {

    private static UserData userData;

    @Autowired
    public ChangeUsdCourseProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                userData = new UserData();
                responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, BotKeyboard.BUY_OR_SELL);
                userService.nextStep(chatId, Command.CHANGE_USD_COURSE);
                break;
            case 1:
                if (!hasMessageText(update, BotStringConstants.BUY_OR_SELL)) {
                    return;
                }
                String dealTypeString = UpdateUtil.getMessageText(update);
                DealType dealType;
                if (BotStringConstants.BUY.equals(dealTypeString)) {
                    dealType = DealType.BUY;
                } else if (BotStringConstants.SELL.equals(dealTypeString)) {
                    dealType = DealType.SELL;
                } else {
                    responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL);
                    return;
                }
                userData.setDealTypeVariable(dealType);
                responseSender.sendMessage(chatId, BotStringConstants.SELECT_CRYPTO_CURRENCY, BotKeyboard.CRYPTO_CURRENCIES);
                userService.nextStep(chatId, Command.CHANGE_USD_COURSE);
                break;
            case 2:
                if (!hasMessageText(update, BotStringConstants.SELECT_CRYPTO_CURRENCY)) {
                    return;
                }
                String cryptoCurrencyString = UpdateUtil.getMessageText(update);
                CryptoCurrency cryptoCurrency;
                if (BotStringConstants.BITCOIN.equals(cryptoCurrencyString)) {
                    cryptoCurrency = CryptoCurrency.BITCOIN;
                } else if (BotStringConstants.LITECOIN.equals(cryptoCurrencyString)) {
                    cryptoCurrency = CryptoCurrency.LITECOIN;
                } else if (BotStringConstants.USDT.equals(cryptoCurrencyString)) {
                    cryptoCurrency = CryptoCurrency.USDT;
                } else if (BotStringConstants.MONERO.equals(cryptoCurrencyString)) {
                    cryptoCurrency = CryptoCurrency.MONERO;
                } else {
                    responseSender.sendMessage(chatId, BotStringConstants.SELECT_CRYPTO_CURRENCY);
                    return;
                }
                userData.setCryptoCurrency(cryptoCurrency);
                responseSender.sendMessage(chatId, BotStringConstants.ENTER_NEW_COURSE, BotKeyboard.CANCEL);
                userService.nextStep(chatId, Command.CHANGE_USD_COURSE);
                break;
            case 3:
                if (!update.hasMessage() || !update.getMessage().hasText()) throw new BaseException("Не найден текст.");
                String newCourseString = UpdateUtil.getMessageText(update).replaceAll(",", ".");
                double newCourse;
                try {
                    newCourse = Double.parseDouble(newCourseString);
                } catch (NumberFormatException e) {
                    responseSender.sendMessage(chatId, BotStringConstants.INCORRECT_VALUE);
                    return;
                }
                BotProperties.BOT_VARIABLE_PROPERTIES.setProperty(
                        BotVariableType.USD_COURSE.getKey(userData.getDealTypeVariable(), userData.getCryptoCurrency()),
                        newCourse);
                responseSender.sendMessage(chatId, BotStringConstants.SUCCESSFUL_COURSE_CHANGE);
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
