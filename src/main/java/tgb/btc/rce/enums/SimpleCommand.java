package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.util.BeanHolder;
import tgb.btc.rce.util.UpdateUtil;

import java.util.function.Consumer;

public enum SimpleCommand {
    NONE(Command.NONE, update -> {
    }),
    BOT_OFFED(Command.BOT_OFFED, update ->
            BeanHolder.responseSender.sendBotMessage(BotMessageType.BOT_OFF, UpdateUtil.getChatId(update))),

    ADMIN_PANEL(Command.ADMIN_PANEL, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.ADMIN_PANEL)),
    OPERATOR_PANEL(Command.OPERATOR_PANEL, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), PropertiesMessage.MENU_MAIN_OPERATOR_MESSAGE, Menu.OPERATOR_PANEL)),

    BACK(Command.BACK, update ->
            BeanHolder.responseSender.sendBotMessage(BotMessageType.START, UpdateUtil.getChatId(update), Menu.MAIN)),

    BOT_SETTINGS(Command.BOT_SETTINGS, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), PropertiesMessage.BOT_SETTINGS_MENU, Menu.BOT_SETTINGS)),

    CONTACTS(Command.CONTACTS, update -> BeanHolder.responseSender.sendBotMessage(BotMessageType.CONTACTS,
            UpdateUtil.getChatId(update),
            BeanHolder.keyboardBuildService.buildContacts(BeanHolder.contactService.findAll()))),

    DISCOUNTS(Command.DISCOUNTS, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), "Меню управления скидками.", Menu.DISCOUNTS)),

    DRAWS(Command.DRAWS, update ->
            BeanHolder.responseSender.sendBotMessage(BotMessageType.DRAWS, UpdateUtil.getChatId(update), Menu.DRAWS)),

    EDIT_CONTACTS(Command.EDIT_CONTACTS, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), PropertiesMessage.EDIT_CONTACTS_MENU, Menu.EDIT_CONTACTS)),

    INLINE_DELETE(Command.INLINE_DELETE, update ->
            BeanHolder.responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId())),

    REPORTS(Command.REPORTS, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), "Меню отчетов", Menu.REPORTS)),

    REQUESTS(Command.REQUESTS, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), "Меню заявок", Menu.REQUESTS)),

    ROULETTE(Command.ROULETTE, update ->
            BeanHolder.responseSender.sendBotMessage(BotMessageType.ROULETTE, UpdateUtil.getChatId(update))),

    SEND_MESSAGES(Command.SEND_MESSAGES, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), PropertiesMessage.SEND_MESSAGES_MENU, Menu.SEND_MESSAGES)),

    USERS(Command.USERS, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update), "Меню для работы с пользователем", Menu.USERS)),

    CABINET(Command.CABINET, update ->
            BeanHolder.responseSender.sendMessage(UpdateUtil.getChatId(update),
                    BeanHolder.messagePropertiesService.getMessage("menu.main.cabinet.message"), BeanHolder.keyboardService.getCabinetButtons())),
    CHAT_ID(Command.CHAT_ID, update -> {
        Long chatId = UpdateUtil.getChatId(update);
        BeanHolder.responseSender.sendMessage(chatId, "Ваш chat id - <code>" + chatId + "</code>.\nНажмите на chat id для копирования в буфер обмена.", "html");
    });

    final Command command;

    final Consumer<Update> consumer;

    SimpleCommand(Command command, Consumer<Update> consumer) {
        this.command = command;
        this.consumer = consumer;
    }

    public Command getCommand() {
        return command;
    }

    public Consumer<Update> getConsumer() {
        return consumer;
    }

    public static SimpleCommand getByCommand(Command command) {
        for (SimpleCommand simpleCommand : SimpleCommand.values()) {
            if (simpleCommand.getCommand().equals(command)) return simpleCommand;
        }
        throw new BaseException("Не найдена SimpleCommand для " + command.name());
    }

    public static void run(Command command, Update update) {
        getByCommand(command).getConsumer().accept(update);
    }
}
