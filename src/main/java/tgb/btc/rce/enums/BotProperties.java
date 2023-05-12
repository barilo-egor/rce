package tgb.btc.rce.enums;

import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.service.PropertiesReader;

public enum BotProperties implements PropertiesReader {

    ANTI_SPAM_PROPERTIES(FilePaths.ANTI_SPAM_PROPERTIES, ','),
    BOT_CONFIG_PROPERTIES(FilePaths.BOT_PROPERTIES, ','),
    BOT_VARIABLE_PROPERTIES(FilePaths.BOT_VARIABLE_PROPERTIES, ','),
    BULK_DISCOUNT_PROPERTIES(FilePaths.BULK_DISCOUNT_PROPERTIES, ','),
    MESSAGE_PROPERTIES(FilePaths.MESSAGE_PROPERTIES, ','),
    TURNING_CURRENCIES_PROPERTIES(FilePaths.CURRENCIES_TURNING, ',');

    private final String fileName;

    private final char listDelimiter;

    BotProperties(String fileName, char listDelimiter) {
        this.fileName = fileName;
        this.listDelimiter = listDelimiter;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public char getListDelimiter() {
        return listDelimiter;
    }

}
