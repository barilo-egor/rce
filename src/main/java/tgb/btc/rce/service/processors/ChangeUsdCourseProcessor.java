package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.CHANGE_USD_COURSE)
public class ChangeUsdCourseProcessor extends Processor {

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
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
                userDataRepository.updateDealTypeByUserChatId(chatId, dealType);
                responseSender.sendMessage(chatId, BotStringConstants.SELECT_CRYPTO_CURRENCY, BotKeyboard.CRYPTO_CURRENCIES);
                userService.nextStep(chatId);
                break;
            case 2:
                if (!hasMessageText(update, BotStringConstants.SELECT_CRYPTO_CURRENCY)) {
                    return;
                }
                String cryptoCurrencyString = UpdateUtil.getMessageText(update);
                CryptoCurrency cryptoCurrency;
                try {
                    cryptoCurrency = CryptoCurrency.fromDisplayName(cryptoCurrencyString);
                } catch (EnumTypeNotFoundException e) {
                    responseSender.sendMessage(chatId, BotStringConstants.SELECT_CRYPTO_CURRENCY);
                    return;
                }
                userDataRepository.updateCryptoCurrencyByChatId(chatId, cryptoCurrency);
                if (!FiatCurrencyUtil.isFew()) {
                    responseSender.sendMessage(chatId, BotStringConstants.ENTER_NEW_COURSE, BotKeyboard.REPLY_CANCEL);
                    userService.nextStep(chatId);
                } else {
                    List<ReplyButton> buttons = Arrays.stream(FiatCurrency.values())
                            .map(fiatCurrency -> ReplyButton.builder().text(fiatCurrency.name()).build())
                            .collect(Collectors.toList());
                    buttons.add(BotReplyButton.CANCEL.getButton());
                    responseSender.sendMessage(chatId, BotStringConstants.SELECT_FIAT_CURRENCY, KeyboardUtil.buildReply(buttons));
                }
                userService.nextStep(chatId);
                break;
            case 3:
                if (!hasMessageText(update, BotStringConstants.SELECT_CRYPTO_CURRENCY)) {
                    return;
                }
                String fiatCurrencyString = UpdateUtil.getMessageText(update);
                FiatCurrency fiatCurrency;
                try {
                    fiatCurrency = FiatCurrency.valueOf(fiatCurrencyString);
                } catch (EnumTypeNotFoundException e) {
                    responseSender.sendMessage(chatId, BotStringConstants.SELECT_CRYPTO_CURRENCY);
                    return;
                }
                userDataRepository.updateStringByUserChatId(chatId, fiatCurrency.name());
                responseSender.sendMessage(chatId, BotStringConstants.ENTER_NEW_COURSE, BotKeyboard.REPLY_CANCEL);
                userService.nextStep(chatId);
                break;
            case 4:
                if (!update.hasMessage() || !update.getMessage().hasText()) throw new BaseException("Не найден текст.");
                String newCourseString = UpdateUtil.getMessageText(update).replaceAll(",", ".");
                double newCourse;
                try {
                    newCourse = Double.parseDouble(newCourseString);
                } catch (NumberFormatException e) {
                    responseSender.sendMessage(chatId, BotStringConstants.INCORRECT_VALUE);
                    return;
                }
                fiatCurrency = FiatCurrencyUtil.isFew()
                        ? FiatCurrency.valueOf(userDataRepository.getStringByUserChatId(chatId))
                        : FiatCurrencyUtil.getFirst();
                BotProperties.BOT_VARIABLE_PROPERTIES.setProperty(BotVariableType.USD_COURSE.getKey() + "."
                        + fiatCurrency.getCode() + "."
                        + userDataRepository.getDealTypeByChatId(chatId).getKey() + "."
                        + userDataRepository.getCryptoCurrencyByChatId(chatId).getShortName(), newCourse);
                responseSender.sendMessage(chatId, BotStringConstants.SUCCESSFUL_COURSE_CHANGE);
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
