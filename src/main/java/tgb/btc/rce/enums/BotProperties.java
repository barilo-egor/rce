package tgb.btc.rce.enums;

import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.service.IPropertyService;
import tgb.btc.rce.service.PropertiesReader;
import tgb.btc.rce.service.ReviewPriseService;
import tgb.btc.rce.service.impl.BulkDiscountService;

import java.util.Objects;

public enum BotProperties implements PropertiesReader {

    ANTI_SPAM(FilePaths.ANTI_SPAM_PROPERTIES, ',', null),
    BOT_CONFIG(FilePaths.BOT_PROPERTIES, ',', null),
    BOT_VARIABLE(FilePaths.BOT_VARIABLE_PROPERTIES, ',', null),
    BOT_VARIABLE_BUFFER(FilePaths.BOT_VARIABLE_BUFFER_PROPERTIES, ',', null),
    BULK_DISCOUNT(FilePaths.BULK_DISCOUNT_PROPERTIES, ',', new BulkDiscountService()),
    BULK_DISCOUNT_BUFFER(FilePaths.BULK_DISCOUNT_BUFFER_PROPERTIES, ',', null),
    MESSAGE(FilePaths.MESSAGE_PROPERTIES, ',', null),
    MESSAGE_BUFFER(FilePaths.MESSAGE_BUFFER_PROPERTIES, ',', null),
    TURNING_CURRENCIES(FilePaths.CURRENCIES_TURNING_PROPERTIES, ',', null),
    MODULES(FilePaths.MODULES_PROPERTIES, ',', null),

    FUNCTIONS(FilePaths.FUNCTIONS_PROPERTIES, ',', null),
    BUTTONS_DESIGN(FilePaths.BUTTONS_DESIGN_PROPERTIES, ',', null),
    CRYPTO_CURRENCIES_DESIGN(FilePaths.CRYPTO_CURRENCIES_DESIGN_PROPERTIES, ',', null),
    SERVER(FilePaths.SERVER_PROPERTIES, ',', null),
    REVIEW_PRISE(FilePaths.REVIEW_PRISE_PROPERTIES, ';', new ReviewPriseService())
    ;

    private final String fileName;
    private final char listDelimiter;
    private final IPropertyService propertyService;

    BotProperties(String fileName, char listDelimiter, IPropertyService propertyService) {
        this.fileName = fileName;
        this.listDelimiter = listDelimiter;
        this.propertyService = propertyService;
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
    public void load() {
        if (Objects.nonNull(this.propertyService)) propertyService.load();
    }
}
