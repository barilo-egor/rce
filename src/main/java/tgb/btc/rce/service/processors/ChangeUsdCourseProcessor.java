package tgb.btc.rce.service.processors;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_USD_COURSE)
public class ChangeUsdCourseProcessor extends Processor {

    @Autowired
    public ChangeUsdCourseProcessor(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                responseSender.sendMessage(chatId, "Введите новый курс.", BotKeyboard.CANCEL);
                userService.nextStep(chatId, Command.CHANGE_USD_COURSE);
                break;
            case 1:
                Double newCourse = UpdateUtil.getDoubleFromText(update);
                PropertiesConfiguration conf;
                try {
                    conf = new PropertiesConfiguration(FilePaths.BOT_VARIABLE_PROPERTIES);
                } catch (ConfigurationException e) {
                    responseSender.sendMessage(chatId, "Ошибки при открытии " + FilePaths.BOT_VARIABLE_PROPERTIES
                            + ": " + e.getMessage() + "\n" + ExceptionUtils.getFullStackTrace(e));
                    userService.setDefaultValues(chatId);
                    break;
                }
                conf.setProperty(BotVariableType.USD_COURSE.getKey(), newCourse);
                try {
                    conf.save();
                    responseSender.sendMessage(chatId, "Курс обновлен.");
                    processToAdminMainPanel(chatId);
                    BotVariablePropertiesUtil.loadProperties();
                } catch (ConfigurationException e) {
                    responseSender.sendMessage(chatId, "Ошибки при замене курса: " + e.getMessage() + "\n"
                            + ExceptionUtils.getFullStackTrace(e));
                    userService.setDefaultValues(chatId);
                    processToAdminMainPanel(chatId);
                }
                break;
        }
    }
}
