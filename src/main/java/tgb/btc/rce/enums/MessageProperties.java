package tgb.btc.rce.enums;

import tgb.btc.library.constants.strings.FilePaths;
import tgb.btc.library.util.system.PropertiesReader;

public enum MessageProperties implements PropertiesReader {
    MESSAGE(FilePaths.MESSAGE_PROPERTIES, ','),
    MESSAGE_BUFFER(FilePaths.MESSAGE_BUFFER_PROPERTIES, ','),
    INFO_MESSAGE(FilePaths.INFO_MESSAGE_PROPERTIES, ',');

    private final String fileName;
    private final char listDelimiter;

    MessageProperties(String fileName, char listDelimiter) {
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
