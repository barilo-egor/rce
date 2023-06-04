package tgb.btc.rce.enums;

import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.service.IValidateService;
import tgb.btc.rce.service.PropertiesReader;
import tgb.btc.rce.service.impl.BulkDiscountValidateService;
import tgb.btc.rce.service.impl.CurrenciesTurningValidateService;

import java.util.Objects;

public enum BotProperties implements PropertiesReader {

    ANTI_SPAM_PROPERTIES(FilePaths.ANTI_SPAM_PROPERTIES, ',', null, false),
    BOT_CONFIG_PROPERTIES(FilePaths.BOT_PROPERTIES, ',', null, false),
    BOT_VARIABLE_PROPERTIES(FilePaths.BOT_VARIABLE_PROPERTIES, ',', null, false),
    BOT_VARIABLE_BUFFER_PROPERTIES(FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES, ',', null, true),
    BULK_DISCOUNT_PROPERTIES(FilePaths.BULK_DISCOUNT_PROPERTIES, ',', new BulkDiscountValidateService(),
                             false),
    BULK_DISCOUNT_BUFFER_PROPERTIES(FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES, ',', new BulkDiscountValidateService(),
                                    true),
    MESSAGE_PROPERTIES(FilePaths.MESSAGE_PROPERTIES, ',', null, false),
    MESSAGE_BUFFER_PROPERTIES(FilePaths.MESSAGE_BUFFER_PROPERTIES, ',', null, true),
    TURNING_CURRENCIES_PROPERTIES(FilePaths.CURRENCIES_TURNING, ',', new CurrenciesTurningValidateService(), false),
    MODULES_PROPERTIES(FilePaths.MODULES_PROPERTIES, ',', null, false),

    FUNCTIONS_PROPERTIES(FilePaths.FUNCTIONS_PROPERTIES, ',', null, false),
    ;

    private final String fileName;
    private final char listDelimiter;
    private final IValidateService validateService;
    private final boolean isBufferProperties;

    BotProperties(String fileName, char listDelimiter, IValidateService validateService, boolean isBufferProperties) {
        this.fileName = fileName;
        this.listDelimiter = listDelimiter;
        this.isBufferProperties = isBufferProperties;
        if (Objects.isNull(validateService)) {
            this.validateService = new IValidateService() {
                @Override
                public void validate(BotProperties botProperties) throws PropertyValueNotFoundException {
                    IValidateService.super.validate(botProperties);
                }
                @Override
                public void load() {
                    IValidateService.super.load();
                }
            };
        } else {
            this.validateService = validateService;
        }
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public char getListDelimiter() {
        return listDelimiter;
    }

    @Override
    public boolean getIsBufferProperties() {
        return isBufferProperties;
    }

    @Override
    public void validate() {
        validateService.validate(this);
    }

    @Override
    public void load() {
        validateService.load();
    }
}
