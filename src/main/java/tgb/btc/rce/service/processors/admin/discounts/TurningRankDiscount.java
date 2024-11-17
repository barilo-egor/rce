package tgb.btc.rce.service.processors.admin.discounts;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

import java.io.File;


@CommandProcessor(command = Command.TURNING_RANK_DISCOUNT)
public class TurningRankDiscount extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Boolean newValue = Boolean.valueOf(values[1]);
        PropertiesConfiguration conf;
        try {
            File file = new File(PropertiesPath.VARIABLE_PROPERTIES.getFileName());
            ListDelimiterHandler delimiter = new DefaultListDelimiterHandler(PropertiesPath.VARIABLE_PROPERTIES.getListDelimiter());

            PropertiesBuilderParameters propertyParameters = new Parameters().properties();
            propertyParameters.setFile(file);
            propertyParameters.setThrowExceptionOnMissing(true);
            propertyParameters.setListDelimiterHandler(delimiter);

            FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(
                    PropertiesConfiguration.class);
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
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        processToAdminMainPanel(chatId);
        PropertiesPath.VARIABLE_PROPERTIES.reload();
    }

}
