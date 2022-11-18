package tgb.btc.rce.service.processors.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentConfig;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.exception.NumberParseException;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SellService {

    private static final List<InlineButton> CURRENCIES = new ArrayList<>();
    private static final List<InlineButton> PAYMENT_TYPES = new ArrayList<>();

    public static final String USE_PROMO = "use_promo";
    public static final String DONT_USE_PROMO = "dont_use_promo";

    private final ResponseSender responseSender;
    private final UserService userService;
    private final DealService dealService;
    private final PaymentConfigService paymentConfigService;

    private final BotMessageService botMessageService;

    @Autowired
    public SellService(ResponseSender responseSender, UserService userService, DealService dealService,
                       PaymentConfigService paymentConfigService, BotMessageService botMessageService) {
        this.responseSender = responseSender;
        this.userService = userService;
        this.dealService = dealService;
        this.paymentConfigService = paymentConfigService;
        this.botMessageService = botMessageService;
    }

    static {
        Arrays.stream(CryptoCurrency.values())
                .forEach(currency -> CURRENCIES.add(InlineButton.builder()
                        .text(currency.getDisplayName())
                        .data(currency.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build()));
        CURRENCIES.add(KeyboardUtil.INLINE_BACK_BUTTON);
    }

    public void createDeal(Long chatId) {
        Deal deal = new Deal();
        deal.setActive(false);
        deal.setPassed(false);
        deal.setDateTime(LocalDateTime.now());
        deal.setDate(LocalDate.now());
        deal.setDealType(DealType.SELL);
        deal.setUser(userService.findByChatId(chatId));
        Deal savedDeal = dealService.save(deal);
        userService.updateCurrentDealByChatId(savedDeal.getPid(), chatId);
    }

    public void askForCurrency(Long chatId) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.CHOOSE_CURRENCY_SELL),
                KeyboardUtil.buildInline(CURRENCIES));
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }


    public void askForSum(Long chatId, CryptoCurrency currency, DealType dealType) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId,
                String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM),
                        dealService.getCryptoCurrencyByPid(
                                userService.getCurrentDealByChatId(chatId))), getCalculatorKeyboard(currency, dealType));
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }

    private ReplyKeyboard getCalculatorKeyboard(CryptoCurrency currency, DealType dealType) {
        String operation = DealType.BUY.equals(dealType) ? "-buy" : "-sell";
        return KeyboardUtil.buildInlineDiff(List.of(
                InlineButton.builder()
                        .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                        .text("Калькулятор")
                        .data(currency.getShortName() + operation + " ")
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON), 1);
    }

    public void saveSum(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        Double sum = UpdateUtil.getDoubleFromText(update);
        CryptoCurrency cryptoCurrency = dealService.getCryptoCurrencyByPid(currentDealPid);
        Double minSum = BotVariablePropertiesUtil.getMinSum(cryptoCurrency);

        if (sum < minSum) {
            responseSender.sendMessage(chatId, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + BigDecimal.valueOf(minSum).stripTrailingZeros().toPlainString() + ".");
            return;
        }

        dealService.updateCryptoAmountByPid(BigDecimal.valueOf(sum), currentDealPid);
        dealService.updateAmountByPid(ConverterUtil.convertCryptoToRub(cryptoCurrency, sum, DealType.SELL), currentDealPid);
        dealService.updateCommissionByPid(ConverterUtil.getCommissionForSell(BigDecimal.valueOf(sum), cryptoCurrency, dealService.getDealTypeByPid(currentDealPid)), currentDealPid);
    }

    public void convertToRub(Update update, Long currentDealPid) {
        System.out.println();
        String query = update.getInlineQuery().getQuery();
        BigDecimal sum;
        CryptoCurrency currency = null;

        if (!hasInputSum(query)) {
            askForCryptoSum(update);
            return;
        }

        try {
            currency = CryptoCurrency.fromShortName(query.substring(0, query.indexOf("-")));
            sum = BigDecimal.valueOf(NumberUtil.getInputDouble(query.substring(query.indexOf(" ") + 1)));
        } catch (EnumTypeNotFoundException e) {
            askForCryptoSum(update);
            return;
        } catch (NumberParseException e) {
            if (hasInputSum(currency, query)) sendInlineAnswer(update, e.getMessage(), false);
            else askForCryptoSum(update);
            return;
        }

        CryptoCurrency cryptoCurrency = dealService.getCryptoCurrencyByPid(currentDealPid);
        BigDecimal minSum = BigDecimalUtil.round(BotVariablePropertiesUtil.getMinSum(cryptoCurrency),
                        cryptoCurrency.getScale());

        if (sum.doubleValue() < minSum.doubleValue()) {
            sendInlineAnswer(update, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + minSum.stripTrailingZeros().toPlainString() + ".", false);
            return;
        }
        sum = BigDecimal.valueOf(BigDecimalUtil.round(sum, cryptoCurrency.getScale()).doubleValue());
        double roundedConvertedSum = BigDecimalUtil.round(ConverterUtil.convertCryptoToRub(currency, sum.doubleValue(), DealType.SELL), 0).doubleValue();
        sendInlineAnswer(update, sum.stripTrailingZeros().toPlainString() + " " + currency.getDisplayName() + " ~ " +
                roundedConvertedSum, true);
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
        String sum = update.getInlineQuery().getQuery().contains(" ") ?
                update.getInlineQuery().getQuery().substring(update.getInlineQuery().getQuery().indexOf(" ")) : "Ошибка";
        responseSender.execute(AnswerInlineQuery.builder().inlineQueryId(update.getInlineQuery().getId())
                .result(InlineQueryResultArticle.builder()
                        .id(update.getInlineQuery().getId())
                        .title(answer)
                        .inputMessageContent(InputTextMessageContent.builder()
                                .messageText(sum)
                                .build())
                        .description(text)
                        .build())
                .build());
    }

    public void askForWallet(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, UpdateUtil.getMessage(update).getMessageId());
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        String message = "Введите " + deal.getPaymentType().getDisplayName() + " реквизиты, куда вы "
                + "хотите получить "
                + BigDecimalUtil.round(deal.getAmount(), 0).stripTrailingZeros().toPlainString() + "₽";

        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, message,
                KeyboardUtil.buildInline(List.of(KeyboardUtil.INLINE_BACK_BUTTON)));
        optionalMessage.ifPresent(sentMessage -> userService.updateBufferVariable(chatId, sentMessage.getMessageId().toString()));
    }

    public void saveWallet(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        String wallet = UpdateUtil.getMessageText(update);
        dealService.updateWalletByPid(wallet, userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
    }

    public void askForPaymentType(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
        BigDecimal dealCryptoAmount = deal.getCryptoAmount().setScale(deal.getCryptoCurrency().getScale(),
                RoundingMode.HALF_UP).stripTrailingZeros();
        BigDecimal dealAmount = deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
        String displayCurrencyName = deal.getCryptoCurrency().getDisplayName();
        String additionalText;
        try {
            additionalText = botMessageService.findByTypeThrows(BotMessageType.ADDITIONAL_DEAL_TEXT).getText() + "\n\n";
        } catch (BaseException e) {
            additionalText = StringUtils.EMPTY;
        }
        String message = "<b>Информация по заявке</b>\n"
                + "\uD83D\uDCAC<b>Продажа " + displayCurrencyName + "</b>: " + dealCryptoAmount.stripTrailingZeros().toPlainString()
                + "\n\n"
                + "\uD83D\uDCB5<b>Сумма перевода</b>: " + dealAmount.stripTrailingZeros().toPlainString() + "₽"
                + "\n\n"
                + additionalText
                + "<b>Выберите способ получения перевода:</b>";

        List<InlineButton> buttons = Arrays.stream(PaymentType.values()).map(paymentType -> {
                    PaymentConfig paymentConfig = paymentConfigService.getByPaymentType(paymentType);
                    if (paymentConfig == null || paymentConfig.getOn()) return InlineButton.builder()
                            .text(paymentType.getDisplayName())
                            .data(paymentType.name())
                            .inlineType(InlineType.CALLBACK_DATA)
                            .build();
                    else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        buttons.add(KeyboardUtil.INLINE_BACK_BUTTON);

        ReplyKeyboard keyboard = KeyboardUtil.buildInlineDiff(buttons);
        responseSender.sendMessage(chatId, message, keyboard, "HTML");
    }

    public void savePaymentType(Update update) {
        if (!update.hasCallbackQuery()) return;
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        PaymentType paymentType = PaymentType.valueOf(update.getCallbackQuery().getData());
        dealService.updatePaymentTypeByPid(paymentType, userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
    }

    public void buildDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
        CryptoCurrency currency = deal.getCryptoCurrency();
        PaymentConfig paymentConfig = paymentConfigService.getByPaymentType(deal.getPaymentType());
        if (paymentConfig == null)
            throw new BaseException("Не установлены реквизиты для " + deal.getPaymentType().getDisplayName() + ".");
        String promoCodeText = Boolean.TRUE.equals(deal.getUsedPromo()) ?
                "\n\n<b> Использован скидочный промокод</b>: "
                        + BotVariablePropertiesUtil.getVariable(BotVariableType.PROMO_CODE_NAME) + "\n\n"
                : "\n\n";

        String walletRequisites;
        switch (deal.getCryptoCurrency()) {
            case BITCOIN:
                walletRequisites = BotVariablePropertiesUtil.getVariable(BotVariableType.WALLET_BTC);
                break;
            case LITECOIN:
                walletRequisites = BotVariablePropertiesUtil.getVariable(BotVariableType.WALLET_LTC);
                break;
            case USDT:
                walletRequisites = BotVariablePropertiesUtil.getVariable(BotVariableType.WALLET_USDT);
                break;
            default:
                throw new BaseException("Не найдены реквизиты крипто кошелька.");
        }
        Rank rank = Rank.getByDealsNumber(dealService.getCountPassedByUserChatId(chatId).intValue());
        if (!Rank.FIRST.equals(rank)) {
            BigDecimal commission = deal.getCommission();
            BigDecimal rankDiscount = BigDecimalUtil.multiplyHalfUp(commission, ConverterUtil.getPercentsFactor(BigDecimal.valueOf(rank.getPercent())));
            deal.setAmount(BigDecimalUtil.addHalfUp(deal.getAmount(), rankDiscount));
            deal = dealService.save(deal);
        }
        String message = "✅<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана."
                + "\n\n"
                + "<b>Продаете</b>: "
                + BigDecimalUtil.round(deal.getCryptoAmount(), currency.getScale()).stripTrailingZeros().toPlainString() + " " + currency.getShortName()
                + "\n"
                + "<b>" + deal.getPaymentType().getDisplayName() + " реквизиты</b>:" + "<code>" + deal.getWallet() + "</code>"
                + "\n\n"
                + "Ваш ранг: " + rank.getSmile() + ", скидка " + rank.getPercent() + "%"
                + "\n\n"
                + "\uD83D\uDCB5<b>Получаете</b>: <code>" + BigDecimalUtil.round(deal.getAmount(), 0).stripTrailingZeros().toPlainString() + "₽</code>"
                + "\n"
                + "<b>Резквизиты для перевода " + currency.getShortName() + ":</b>"
                + "\n\n"
                + "<code>" + walletRequisites + "</code>"
                + "\n\n"
                + "⏳<b>Заявка действительна</b>: " + BotVariablePropertiesUtil.getVariable(BotVariableType.DEAL_ACTIVE_TIME) + " минут"
                + "\n\n"
                + "☑️После успешного перевода денег по указанному кошельку нажмите на кнопку <b>\""
                + Command.PAID.getText() + "\"</b> или же вы можете отменить данную заявку, нажав на кнопку <b>\""
                + Command.CANCEL_DEAL.getText() + "\"</b>."
                + promoCodeText;

        ReplyKeyboard keyboard = KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text(Command.PAID.getText())
                        .data(Command.PAID.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text(Command.CANCEL.getText())
                        .data(Command.CANCEL_DEAL.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build()
        ));
        responseSender.sendMessage(chatId, message, keyboard, "HTML");
    }

    public void confirmDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        dealService.updateIsActiveByPid(true, currentDealPid);
        userService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_CONFIRMED));
        userService.getAdminsChatIds().forEach(adminChatId ->
                responseSender.sendMessage(adminChatId, "Поступила новая заявка на продажу.",
                        KeyboardUtil.buildInline(List.of(
                                InlineButton.builder()
                                        .text(Command.SHOW_DEAL.getText())
                                        .data(Command.SHOW_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                                + currentDealPid)
                                        .build()
                        ))));
        userService.updateCurrentDealByChatId(null, chatId);
    }
}
