package tgb.btc.rce.service.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

public interface IKeyboardBuildService {

    InlineKeyboardMarkup buildInline(List<InlineButton> buttons);

    InlineKeyboardMarkup buildInline(List<InlineButton> buttons, int maxNumberOfColumns);

    InlineKeyboardMarkup buildInlineByRows(List<List<InlineKeyboardButton>> rows);

    InlineKeyboardMarkup buildInlineSingleLast(List<InlineButton> buttons, int maxNumberOfColumns, InlineButton inlineButton);

    List<List<InlineKeyboardButton>> buildInlineRows(List<InlineButton> buttons, int maxNumberOfColumns);

    ReplyKeyboardMarkup buildReply(List<ReplyButton> buttons);

    ReplyKeyboardMarkup buildReply(int maxNumberOfColumns, List<ReplyButton> buttons);

    ReplyKeyboardMarkup buildReply(int maxNumberOfColumns, List<ReplyButton> buttons, boolean oneTime);

    ReplyKeyboardMarkup buildReply(int maxNumberOfColumns, boolean oneTime, boolean resize, List<ReplyButton> buttons);

    InlineKeyboardMarkup buildContacts(List<Contact> contacts);

    InlineButton createCallBackDataButton(String text, Command command, String... string);

    InlineKeyboardMarkup getLink(String text, String data);
}
