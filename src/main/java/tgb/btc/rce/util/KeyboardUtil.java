package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import tgb.btc.rce.bean.Contact;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class KeyboardUtil {
    private KeyboardUtil() {
    }

    public static InlineButton INLINE_BACK_BUTTON = InlineButton.builder().text("Назад")
            .inlineType(InlineType.CALLBACK_DATA)
            .data(Command.BACK.getText())
            .build();

    public static InlineKeyboardMarkup buildInline(List<InlineButton> buttons) {
        return buildInline(buttons, 1);
    }

    public static InlineKeyboardMarkup buildInline(List<InlineButton> buttons, int numberOfColumns) {

        return InlineKeyboardMarkup.builder()
                .keyboard(buildInlineRows(buttons, numberOfColumns))
                .build();
    }

    public static InlineKeyboardMarkup buildInlineByRows(List<List<InlineKeyboardButton>> rows) {

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static InlineKeyboardMarkup buildInlineSingleLast(List<InlineButton> buttons, int numberOfColumns, InlineButton inlineButton) {
        List<List<InlineKeyboardButton>> builtRows = buildInlineRows(buttons, numberOfColumns);
        builtRows.add(List.of(parse(inlineButton)));
        return InlineKeyboardMarkup.builder().keyboard(builtRows).build();
    }

    public static List<List<InlineKeyboardButton>> buildInlineRows(List<InlineButton> buttons, int numberOfColumns) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int j = 0;

        for (int i = 0; i < buttons.size(); i++) {
            row.add(parse(buttons.get(i)));
            j++;
            if (j == numberOfColumns || i == (buttons.size() - 1)) {
                rows.add(row);
                row = new ArrayList<>();
                j = 0;
            }
        }
        return rows;
    }

    private static InlineKeyboardButton parse(InlineButton inlineButton) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(inlineButton.getText());
        String data = inlineButton.getData();
        InlineType inlineType = inlineButton.getInlineType();
        switch (Objects.isNull(inlineType) ? InlineType.CALLBACK_DATA : inlineType) {
            case URL:
                inlineKeyboardButton.setUrl(data);
                break;
            case CALLBACK_DATA:
                inlineKeyboardButton.setCallbackData(data);
                break;
            case SWITCH_INLINE_QUERY:
                inlineKeyboardButton.setSwitchInlineQuery(data);
                break;
            case SWITCH_INLINE_QUERY_CURRENT_CHAT:
                inlineKeyboardButton.setSwitchInlineQueryCurrentChat(data);
                break;
            case WEB_APP:
                inlineKeyboardButton.setWebApp(WebAppInfo.builder().url(data).build());
                break;
        }
        return inlineKeyboardButton;
    }

    public static ReplyKeyboardMarkup buildReply(List<ReplyButton> buttons) {
        return buildReply(1, false, true, buttons);
    }

    public static ReplyKeyboardMarkup buildReply(int numberOfColumns, List<ReplyButton> buttons) {
        return buildReply(numberOfColumns, false, true, buttons);
    }

    public static ReplyKeyboardMarkup buildReply(int numberOfColumns, List<ReplyButton> buttons, boolean oneTime) {
        return buildReply(numberOfColumns, oneTime, true, buttons);
    }

    public static ReplyKeyboardMarkup buildReply(List<ReplyButton> buttons, boolean oneTime) {
        return buildReply(1, oneTime, true, buttons);
    }

    public static ReplyKeyboardMarkup buildReply(int numberOfColumns, boolean oneTime, boolean resize, List<ReplyButton> buttons) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        int j = 0;

        for (int i = 0; i < buttons.size(); i++) {
            KeyboardButton keyboardButton = KeyboardButton.builder()
                    .text(buttons.get(i).getText())
                    .build();
            if (buttons.get(i).isRequestContact()) keyboardButton.setRequestContact(true);
            else if (buttons.get(i).isRequestLocation()) keyboardButton.setRequestLocation(true);
            row.add(keyboardButton);
            j++;
            if (j == numberOfColumns || i == (buttons.size() - 1)) {
                rows.add(row);
                row = new KeyboardRow();
                j = 0;
            }
        }
        return ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(oneTime)
                .resizeKeyboard(resize)
                .keyboard(rows)
                .build();
    }

    public static ReplyButton[] getCryptoCurrencyButtons() {
        ReplyButton[] replyButtons = new ReplyButton[CryptoCurrency.values().length + 1];
        int i = 0;
        for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
            ReplyButton replyButton = ReplyButton.builder()
                    .text(cryptoCurrency.getDisplayName())
                    .build();
            replyButtons[i] = replyButton;
            i++;
        }
        replyButtons[i] = BotReplyButton.CANCEL.getButton();
        return replyButtons;
    }

    public static ReplyKeyboard buildContacts(List<Contact> contacts) {
        return KeyboardUtil.buildInline(
                contacts.stream()
                        .map(contact -> InlineButton.builder()
                                .text(contact.getLabel())
                                .data(contact.getUrl())
                                .inlineType(InlineType.URL)
                                .build())
                        .collect(Collectors.toList()));
    }

   public static InlineButton createCallBackDataButton(String text, Command command, String... string) {
        return InlineButton.builder()
                .inlineType(InlineType.CALLBACK_DATA)
                .text(text)
                .data(CallbackQueryUtil.buildCallbackData(command, string))
                .build();
   }

    public static InlineButton createCallBackDataButton (InlineCalculatorButton inlineCalculatorButton) {
        return KeyboardUtil.createCallBackDataButton(inlineCalculatorButton.getData(), Command.INLINE_CALCULATOR, inlineCalculatorButton.getData());
    }
}
