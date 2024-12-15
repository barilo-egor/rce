package tgb.btc.rce.service.impl.keyboard;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.BotInlineButton;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class KeyboardBuildService implements IKeyboardBuildService {

    private final ICallbackDataService callbackDataService;

    public KeyboardBuildService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Override
    public InlineButton getInlineBackButton() {
        return BotInlineButton.CANCEL.getButton();
    }

    @Override
    public InlineKeyboardMarkup buildInline(List<InlineButton> buttons) {
        return buildInline(buttons, 1);
    }

    @Override
    public InlineKeyboardMarkup buildInline(List<InlineButton> buttons, int maxNumberOfColumns) {
        return InlineKeyboardMarkup.builder()
                .keyboard(buildInlineRows(buttons, maxNumberOfColumns))
                .build();
    }

    @Override
    public InlineKeyboardMarkup buildInlineByRows(List<List<InlineKeyboardButton>> rows) {
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    @Override
    public InlineKeyboardMarkup buildInlineSingleLast(List<InlineButton> buttons, int maxNumberOfColumns, InlineButton inlineButton) {
        List<List<InlineKeyboardButton>> builtRows = buildInlineRows(buttons, maxNumberOfColumns);
        builtRows.add(List.of(parse(inlineButton)));
        return InlineKeyboardMarkup.builder().keyboard(builtRows).build();
    }

    @Override
    public List<List<InlineKeyboardButton>> buildInlineRows(List<InlineButton> buttons, int maxNumberOfColumns) {
        if (maxNumberOfColumns < 1)
            throw new BaseException("Количество колонок не может быть меньше одного.");
        if (CollectionUtils.isEmpty(buttons))
            throw new BaseException("Должна присутствовать хотя бы одна кнопка");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < buttons.size(); i++) {
            row.add(parse(buttons.get(i)));
            j++;
            if (j == maxNumberOfColumns || i == (buttons.size() - 1)) {
                rows.add(row);
                row = new ArrayList<>();
                j = 0;
            }
        }
        return rows;
    }

    private InlineKeyboardButton parse(InlineButton inlineButton) {
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

    @Override
    public ReplyKeyboardMarkup buildReply(List<ReplyButton> buttons) {
        return buildReply(1, false, true, buttons);
    }

    @Override
    public ReplyKeyboardMarkup buildReply(int maxNumberOfColumns, List<ReplyButton> buttons) {
        return buildReply(maxNumberOfColumns, false, true, buttons);
    }

    @Override
    public ReplyKeyboardMarkup buildReply(int maxNumberOfColumns, List<ReplyButton> buttons, boolean oneTime) {
        return buildReply(maxNumberOfColumns, oneTime, true, buttons);
    }

    @Override
    public ReplyKeyboardMarkup buildReply(int maxNumberOfColumns, boolean oneTime, boolean resize, List<ReplyButton> buttons) {
        if (maxNumberOfColumns < 1)
            throw new BaseException("Количество колонок не может быть меньше 1.");
        if (CollectionUtils.isEmpty(buttons))
            throw new BaseException("Должна присутствовать хотя бы одна кнопка.");
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
            if (j == maxNumberOfColumns || i == (buttons.size() - 1)) {
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

    @Override
    public InlineKeyboardMarkup buildContacts(List<Contact> contacts) {
        return buildInline(
                contacts.stream()
                        .map(contact -> InlineButton.builder()
                                .text(contact.getLabel())
                                .data(contact.getUrl())
                                .inlineType(InlineType.URL)
                                .build())
                        .toList());
    }

    @Override
    public InlineButton createCallBackDataButton(String text, CallbackQueryData callbackQueryData, String... string) {
        return InlineButton.builder()
                .inlineType(InlineType.CALLBACK_DATA)
                .text(text)
                .data(callbackDataService.buildData(callbackQueryData, string))
                .build();
    }

    @Override
    public InlineKeyboardMarkup getLink(String text, String data) {
        return buildInline(List.of(
                InlineButton.builder()
                        .text(text)
                        .data(data)
                        .inlineType(InlineType.URL)
                        .build()));
    }
}
