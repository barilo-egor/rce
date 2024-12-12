package tgb.btc.rce.service.handler.impl.callback.discount;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.io.File;

@Service
public class TurningRankDiscountHandler implements ICallbackQueryHandler {

    private final VariablePropertiesReader variablePropertiesReader;

    private final IModifyUserService modifyUserService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public TurningRankDiscountHandler(VariablePropertiesReader variablePropertiesReader,
                                      IModifyUserService modifyUserService, IResponseSender responseSender,
                                      ICallbackDataService callbackDataService) {
        this.variablePropertiesReader = variablePropertiesReader;
        this.modifyUserService = modifyUserService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        boolean newValue = Boolean.parseBoolean(callbackDataService.getArgument(callbackQuery.getData(), 1));
        PropertiesConfiguration conf;
        try {
            File file = new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName());
            ListDelimiterHandler delimiter = new DefaultListDelimiterHandler(PropertiesPath.VARIABLE_PROPERTIES.getListDelimiter());

            PropertiesBuilderParameters propertyParameters = new Parameters().properties();
            propertyParameters.setFile(file);
            propertyParameters.setThrowExceptionOnMissing(true);
            propertyParameters.setListDelimiterHandler(delimiter);

            FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);
            builder.setAutoSave(true);

            builder.configure(propertyParameters);
            conf = builder.getConfiguration();
        } catch (ConfigurationException e) {
            responseSender.sendMessage(chatId, "Ошибки при открытии " + PropertiesPath.VARIABLE_PROPERTIES.getFileName()
                    + ": " + e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
            modifyUserService.setDefaultValues(chatId);
            return;
        }
        conf.setProperty(VariableType.DEAL_RANK_DISCOUNT_ENABLE.getKey(), newValue);
        responseSender.sendMessage(chatId, newValue ? "Скидка включена." : "Скидка выключена.");
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        variablePropertiesReader.reload();
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.TURNING_RANK_DISCOUNT;
    }
}
