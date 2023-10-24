package tgb.btc.rce.enums.properties;

import tgb.btc.library.constants.strings.FilePaths;
import tgb.btc.library.util.system.PropertiesReader;

public enum BotProperties implements PropertiesReader {

    ANTI_SPAM(FilePaths.ANTI_SPAM_PROPERTIES, ','),
    BOT_CONFIG(FilePaths.BOT_PROPERTIES, ','),

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
