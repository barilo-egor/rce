package tgb.btc.rce.enums;

import tgb.btc.library.constants.strings.FilePaths;
import tgb.btc.library.util.system.PropertiesReader;

public enum BotProperties implements PropertiesReader {

    ANTI_SPAM(FilePaths.ANTI_SPAM_PROPERTIES, ','),
    BOT_CONFIG(FilePaths.BOT_PROPERTIES, ','),
    VARIABLE(FilePaths.VARIABLE_PROPERTIES, ','),
    VARIABLE_BUFFER(FilePaths.VARIABLE_BUFFER_PROPERTIES, ','),
    BULK_DISCOUNT(FilePaths.BULK_DISCOUNT_PROPERTIES, ','),
    BULK_DISCOUNT_BUFFER(FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES, ','),
    TURNING_CURRENCIES(FilePaths.CURRENCIES_TURNING_PROPERTIES, ','),
    MODULES(FilePaths.MODULES_PROPERTIES, ','),

    FUNCTIONS(FilePaths.FUNCTIONS_PROPERTIES, ','),
    REVIEW_PRISE(FilePaths.REVIEW_PRISE_PROPERTIES, ';')
    ;

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
