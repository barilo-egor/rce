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
import tgb.btc.rce.bean.BotMessage;
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
public class ExchangeService {

    private static final List<InlineButton> CURRENCIES = new ArrayList<>();

    public static final String USE_PROMO = "use_promo";
    public static final String DONT_USE_PROMO = "dont_use_promo";

    private final ResponseSender responseSender;
    private final UserService userService;
    private final DealService dealService;
    private final PaymentConfigService paymentConfigService;

    private final BotMessageService botMessageService;

    @Autowired
    public ExchangeService(ResponseSender responseSender, UserService userService, DealService dealService,
                           PaymentConfigService paymentConfigService, BotMessageService botMessageService) {
        this.responseSender = responseSender;
        this.userService = userService;
        this.dealService = dealService;
        this.paymentConfigService = paymentConfigService;
        this.botMessageService = botMessageService;
    }

    static {
        Arrays.asList(CryptoCurrency.values())
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
        deal.setDealType(DealType.BUY);
        deal.setUser(userService.findByChatId(chatId));
        Deal savedDeal = dealService.save(deal);
        userService.updateCurrentDealByChatId(savedDeal.getPid(), chatId);
    }

    public void askForCurrency(Long chatId) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.CHOOSE_CURRENCY),
                KeyboardUtil.buildInline(CURRENCIES));
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }


    public void askForSum(Long chatId, CryptoCurrency currency) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId,
                String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM),
                        dealService.getCryptoCurrencyByPid(
                                userService.getCurrentDealByChatId(chatId))), getCalculatorKeyboard(currency));
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }

    private ReplyKeyboard getCalculatorKeyboard(CryptoCurrency currency) {
        return KeyboardUtil.buildInlineDiff(List.of(
                InlineButton.builder()
                        .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                        .text("Калькулятор")
                        .data(currency.getShortName() + " ")
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
                    + " = " + minSum + ".");
            return;
        }

        dealService.updateCryptoAmountByPid(BigDecimal.valueOf(sum), currentDealPid);
        dealService.updateAmountByPid(ConverterUtil.convertCryptoToRub(cryptoCurrency, sum, DealType.BUY), currentDealPid);
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
        double minSum = BigDecimalUtil.round(BotVariablePropertiesUtil.getMinSum(cryptoCurrency),
                        cryptoCurrency.getScale())
                .doubleValue();

        if (sum < minSum) {
            sendInlineAnswer(update, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + minSum + ".", false);
            return;
        }
        sum = BigDecimalUtil.round(sum, cryptoCurrency.getScale()).doubleValue();
        double roundedConvertedSum = BigDecimalUtil.round(ConverterUtil.convertCryptoToRub(currency, sum, DealType.BUY), 0).doubleValue();
        sendInlineAnswer(update, sum + " " + currency.getDisplayName() + " ~ " +
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

    public void askForUserPromoCode(Long chatId) {
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        double dealCryptoAmount = deal.getCryptoAmount().setScale(deal.getCryptoCurrency().getScale(),
                RoundingMode.HALF_UP).stripTrailingZeros().doubleValue();
        double dealAmount = deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().doubleValue();
        BigDecimal discount = BigDecimal.valueOf(
                        BotVariablePropertiesUtil.getDouble(BotVariableType.PROMO_CODE_DISCOUNT))
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal sumWithDiscount = deal.getAmount().subtract(BigDecimalUtil.multiplyHalfUp(deal.getAmount(),
                        ConverterUtil.getPercentsFactor(discount)))
                .setScale(0, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        String message = "<b>Покупка " + deal.getCryptoCurrency().getDisplayName() + "</b>: " + dealCryptoAmount
                + "\n\n"
                + "<b>Сумма перевода</b>: <s>" + dealAmount + "</s> " + sumWithDiscount
                + "\n\n"
                + "У вас есть промокод: <b>" + BotVariablePropertiesUtil.getVariable(BotVariableType.PROMO_CODE_NAME)
                + "</b>, который даёт скидку в размере " + discount.stripTrailingZeros().doubleValue() + "% от комиссии"
                + "\n\n"
                + "<b>Использовать промокод</b> как скидку?";
        ReplyKeyboard keyboard = KeyboardUtil.buildInlineDiff(List.of(
                InlineButton.builder()
                        .text("Использовать, " + sumWithDiscount.stripTrailingZeros().doubleValue())
                        .data(USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без промокода, " + dealAmount)
                        .data(DONT_USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON
        ));

        responseSender.sendMessage(chatId, message, keyboard, "HTML");
    }

    public void askForWallet(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, UpdateUtil.getMessage(update).getMessageId());
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        String message = "Введите " + deal.getCryptoCurrency().getDisplayName() + "-адрес кошелька, куда вы "
                + "хотите отправить " + BigDecimalUtil.round(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()).doubleValue()
                + " " + deal.getCryptoCurrency().getShortName();

        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, message,
                KeyboardUtil.buildInline(List.of(KeyboardUtil.INLINE_BACK_BUTTON)));
        optionalMessage.ifPresent(sentMessage -> userService.updateBufferVariable(chatId, sentMessage.getMessageId().toString()));
    }

    public void processPromoCode(Update update) {
        if (!update.hasCallbackQuery()) return;
        Long chatId = UpdateUtil.getChatId(update);
        if (USE_PROMO.equals(update.getCallbackQuery().getData())) {
            Long currentDealPid = userService.getCurrentDealByChatId(chatId);
            BigDecimal discount = BigDecimal.valueOf(
                    BotVariablePropertiesUtil.getDouble(BotVariableType.PROMO_CODE_DISCOUNT));
            BigDecimal dealAmount = dealService.getAmountByPid(currentDealPid);
            BigDecimal sumWithDiscount = dealAmount.subtract(BigDecimalUtil.multiplyHalfUp(dealAmount,
                            ConverterUtil.getPercentsFactor(discount)))
                    .setScale(0, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            dealService.updateAmountByPid(sumWithDiscount, currentDealPid);
            dealService.updateIsUsedPromoByPid(true, currentDealPid);
        }
    }

    public void saveWallet(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;
        String wallet = UpdateUtil.getMessageText(update);
        validateWallet(wallet);
        dealService.updateWalletByPid(wallet, userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
    }

    private void validateWallet(String wallet) {
        if (wallet.length() < 33) throw new BaseException("Длина кошелька не может быть меньше 33-х символов.");
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
                + "<b>Покупка " + displayCurrencyName + "</b>: " + dealCryptoAmount + "\n"
                + "<b>" + displayCurrencyName + "-адрес</b>:" + "<code>" + deal.getWallet() + "</code>"
                + "\n\n"
                + "<b>Сумма перевода</b>: " + dealAmount + "₽"
                + "\n\n"
                + additionalText
                + "<b>Выберите способ оплаты:</b>";

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
        if (paymentConfig == null) throw new BaseException("Не установлены реквизиты для " + deal.getPaymentType().getDisplayName() + ".");
        String promoCodeText = Boolean.TRUE.equals(deal.getUsedPromo()) ?
                "\n\n<b> Использован скидочный промокод</b>: "
                        + BotVariablePropertiesUtil.getVariable(BotVariableType.PROMO_CODE_NAME) + "\n\n"
                : "\n\n";

        String message = "<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана."
                + "\n\n"
                + "<b>Получаете</b>: "
                + BigDecimalUtil.round(deal.getCryptoAmount(), currency.getScale()).doubleValue() + " " + currency.getShortName()
                + "\n"
                + "<b>" + deal.getCryptoCurrency().getDisplayName() + "-адрес</b>:" + "<code>" + deal.getWallet() + "</code>"
                + "\n\n"
                + "<b>Сумма к оплате</b>: <code>" + BigDecimalUtil.round(deal.getAmount(), 0).doubleValue() + "₽</code>"
                + "\n"
                + "<b>Резквизиты для оплаты:</b>"
                + "\n\n"
                + "<code>" + paymentConfig.getRequisites() + "</code>"
                + "\n\n"
                + "<b>Заявка действительна</b>: " + BotVariablePropertiesUtil.getVariable(BotVariableType.DEAL_ACTIVE_TIME) + " минут"
                + "\n\n"
                + "После успешного перевода денег по указанным реквизитам нажмите на кнопку <b>\""
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
        dealService.updateIsActiveByPid(true, userService.getCurrentDealByChatId(chatId));
        userService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_CONFIRMED));
        userService.getAdminsChatIds().forEach(adminChatId ->
                responseSender.sendMessage(adminChatId, "Поступила новая заявка на покупку.",
                KeyboardUtil.buildInline(List.of(
                        InlineButton.builder()
                                .text(Command.SHOW_DEAL.getText())
                                .data(Command.SHOW_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                        + userService.getCurrentDealByChatId(chatId))
                                .build()
                ))));
    }
}
