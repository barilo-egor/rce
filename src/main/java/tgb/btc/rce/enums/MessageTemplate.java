package tgb.btc.rce.enums;

public enum MessageTemplate {
    ASK_CHAT_ID("Введите чат айди пользователя.", BotKeyboard.CANCEL);

    final String message;

    final BotKeyboard botKeyboard;

    MessageTemplate(String message, BotKeyboard botKeyboard) {
        this.message = message;
        this.botKeyboard = botKeyboard;
    }

    public String getMessage() {
        return message;
    }

    public BotKeyboard getBotKeyboard() {
        return botKeyboard;
    }
}
