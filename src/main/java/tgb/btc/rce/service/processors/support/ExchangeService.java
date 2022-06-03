package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.exception.NumberParseException;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ExchangeService {

    private static final List<InlineButton> currencies = new ArrayList<>();

    private final ResponseSender responseSender;
    private final UserService userService;
    private final DealService dealService;

    @Autowired
    public ExchangeService(ResponseSender responseSender, UserService userService, DealService dealService) {
        this.responseSender = responseSender;
        this.userService = userService;
        this.dealService = dealService;
    }

    static {
        Arrays.asList(CryptoCurrency.values())
                .forEach(currency -> currencies.add(InlineButton.builder()
                        .text(currency.getDisplayName())
                        .data(currency.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build()));
    }

    public void createDeal(Long chatId) {
        Deal deal = new Deal();
        deal.setActive(false);
        deal.setPassed(false);
        deal.setUser(userService.findByChatId(chatId));
        Deal savedDeal = dealService.save(deal);
        userService.updateCurrentDealByChatId(savedDeal.getPid(), chatId);
    }

    public void askForCurrency(Long chatId) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.CHOOSE_CURRENCY),
                KeyboardUtil.buildInline(currencies));
        userService.nextStep(chatId, Command.BUY_BITCOIN);
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }


    public void askForSum(Long chatId, CryptoCurrency currency) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId,
                String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM),
                        dealService.getCryptoCurrencyByPid(
                                userService.getCurrentDealByChatId(chatId))), getCalculatorKeyboard(currency));
        userService.nextStep(chatId);
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }

    private ReplyKeyboard getCalculatorKeyboard(CryptoCurrency currency) {
        return KeyboardUtil.buildInline(List.of(
                        InlineButton.builder()
                                .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                                .text("Калькулятор")
                                .data(currency.getShortName() + " ")
                                .build()),
                1, InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT);
    }

    public void saveSum(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        Double sum = UpdateUtil.getDoubleFromText(update);
        CryptoCurrency cryptoCurrency = dealService.getCryptoCurrencyByPid(currentDealPid);
        Double minSum = BotVariablePropertiesUtil.getMinSum(cryptoCurrency);

        if (sum < minSum) {
            responseSender.sendMessage(chatId, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + minSum + ".");
            return;
        }

        dealService.updateCryptoAmountByPid(BigDecimal.valueOf(sum), currentDealPid);
        dealService.updateCryptoAmountByPid(ConverterUtil.convertCryptoToRub(cryptoCurrency, sum), currentDealPid);
    }

    public void convertToRub(Update update, Long currentDealPid) {
        System.out.println();
        String query = update.getInlineQuery().getQuery();
        Double sum;
        CryptoCurrency currency = null;

        if (!hasInputSum(query)) {
            askForCryptoSum(update);
            return;
        }

        try {
            currency = CryptoCurrency.fromShortName(query.substring(0, query.indexOf(" ")));
            sum = NumberUtil.getInputDouble(query.substring(query.indexOf(" ") + 1));
        } catch (EnumTypeNotFoundException e) {
            askForCryptoSum(update);
            return;
        } catch (NumberParseException e) {
            if (hasInputSum(currency, query)) sendInlineAnswer(update, e.getMessage(), false);
            else askForCryptoSum(update);
            return;
        }

        CryptoCurrency cryptoCurrency = dealService.getCryptoCurrencyByPid(currentDealPid);
        Double minSum = BotVariablePropertiesUtil.getMinSum(cryptoCurrency);

        if (sum < minSum) {
            sendInlineAnswer(update, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + minSum + ".", false);
            return;
        }
        sendInlineAnswer(update, sum + " " + currency.getDisplayName() + " ~ " +
                ConverterUtil.convertCryptoToRub(currency, sum), true);
    }

    private boolean hasInputSum(CryptoCurrency currency, String query) {
        return currency != null && query.length() > currency.getShortName().length() + 1;
    }

    private boolean hasInputSum(String query) {
        return query.contains(" ");
    }

    private void askForCryptoSum(Update update) {
        sendInlineAnswer(update, BotStringConstants.ENTER_CRYPTO_SUM, false);
    }

    private void sendInlineAnswer(Update update, String answer, boolean textPushButton) {
        String text = textPushButton ? "Нажмите сюда, чтобы отправить сумму" : "Введите сумму в криптовалюте.";
        String title = update.getInlineQuery().getQuery().contains(" ") ?
                update.getInlineQuery().getQuery().substring(update.getInlineQuery().getQuery().indexOf(" ")) : "Ошибка";
        responseSender.execute(AnswerInlineQuery.builder().inlineQueryId(update.getInlineQuery().getId())
                .result(InlineQueryResultArticle.builder()
                        .id(update.getInlineQuery().getId())
                        .title(title)
                        .inputMessageContent(InputTextMessageContent.builder()
                                .messageText(text)
                                .build())
                        .description(answer)
                        .build())
                .build());
    }
}
