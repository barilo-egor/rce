package tgb.btc.rce.service.processors;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.constants.strings.FilePaths;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.TURNING_RANK_DISCOUNT)
public class TurningRankDiscount extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Boolean newValue = Boolean.valueOf(values[1]);
        PropertiesConfiguration conf;
        try {
            conf = new PropertiesConfiguration(FilePaths.VARIABLE_PROPERTIES);
        } catch (ConfigurationException e) {
            responseSender.sendMessage(chatId, "Ошибки при открытии " + FilePaths.VARIABLE_PROPERTIES
                    + ": " + e.getMessage() + "\n" + ExceptionUtils.getFullStackTrace(e));
            userService.setDefaultValues(chatId);
            return;
        }
        conf.setProperty(VariableType.DEAL_RANK_DISCOUNT_ENABLE.getKey(), newValue);
        try {
            conf.save();
            responseSender.sendMessage(chatId, newValue ? "Скидка включена." : "Скидка выключена.");
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            processToAdminMainPanel(chatId);
            CommonProperties.VARIABLE.reload();
        } catch (ConfigurationException e) {
            responseSender.sendMessage(chatId, "Ошибки при включении/выключении ранговой скидки: " + e.getMessage() + "\n"
                    + ExceptionUtils.getFullStackTrace(e));
            userService.setDefaultValues(chatId);
            processToAdminMainPanel(chatId);
        }
    }

}
