package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardUtil {
    private KeyboardUtil() {
    }

    public static InlineKeyboardMarkup buildInline(List<InlineButton> buttons) {
        return buildInline(buttons, 1, InlineType.CALLBACK_DATA);
    }

    public static InlineKeyboardMarkup buildInline(List<InlineButton> buttons, int numberOfColumns) {
        return buildInline(buttons, numberOfColumns, InlineType.CALLBACK_DATA);
    }

    public static InlineKeyboardMarkup buildInline(List<InlineButton> buttons, InlineType inlineType) {
        return buildInline(buttons, 1, inlineType);
    }

    public static InlineKeyboardMarkup buildInline(List<InlineButton> buttons, int numberOfColumns,
                                                   InlineType inlineType) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int j = 0;

        for (int i = 0; i < buttons.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(buttons.get(i).getText());
            String data = buttons.get(i).getData();
            switch (inlineType) {
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
            }
            row.add(inlineKeyboardButton);
            j++;
            if (j == numberOfColumns || i == (buttons.size() - 1)) {
                rows.add(row);
                row = new ArrayList<>();
                j = 0;
            }
        }
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static InlineKeyboardMarkup buildInlineDiff(List<InlineButton> buttons) {
        return buildInlineDiff(buttons, 1);
    }

    public static InlineKeyboardMarkup buildInlineDiff(List<InlineButton> buttons, int numberOfColumns) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int j = 0;

        for (int i = 0; i < buttons.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(buttons.get(i).getText());
            String data = buttons.get(i).getData();
            switch (buttons.get(i).getInlineType()) {
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
            }
            row.add(inlineKeyboardButton);
            j++;
            if (j == numberOfColumns || i == (buttons.size() - 1)) {
                rows.add(row);
                row = new ArrayList<>();
                j = 0;
            }
        }
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
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

    public static ReplyKeyboardMarkup buildReply(int numberOfColumns, boolean oneTime, boolean resize, List<ReplyButton> buttons) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        int j = 0;

        for (int i = 0; i < buttons.size(); i++) {
            row.add(KeyboardButton.builder()
                    .text(buttons.get(i).getText())
                    .requestContact(buttons.get(i).isRequestContact())
                    .requestLocation(buttons.get(i).isRequestContact())
                    .build());
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
}
