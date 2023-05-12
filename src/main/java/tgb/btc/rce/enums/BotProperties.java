package tgb.btc.rce.enums;

import tgb.btc.rce.service.PropertiesReader;

public class BotProperties implements PropertiesReader {



    private final String fileName;

    private final char listDelimiter;

    public BotProperties(String fileName, char listDelimiter) {
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
