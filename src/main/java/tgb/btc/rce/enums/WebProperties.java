package tgb.btc.rce.enums;

import tgb.btc.library.constants.strings.FilePaths;
import tgb.btc.library.util.system.PropertiesReader;

public enum WebProperties implements PropertiesReader {
    SERVER(FilePaths.SERVER_PROPERTIES, ','),
    LOGIN(FilePaths.LOGIN_PROPERTIES, ',');

    private final String fileName;
    private final char listDelimiter;

    WebProperties(String fileName, char listDelimiter) {
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
