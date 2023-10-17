package tgb.btc.rce.enums;

import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.service.PropertiesReader;

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
