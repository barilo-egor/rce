package tgb.btc.rce.service.handler.impl.message.text.slash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.AntiSpamType;
import tgb.btc.rce.enums.CalculatorType;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;
import tgb.btc.rce.service.handler.util.IStartService;

@Service
@Slf4j
public class StartHandler implements ISlashCommandHandler {

    private final IStartService startService;

    private final ApplicationContext applicationContext;

    private final IModule<CalculatorType> calcModule;

    private final IModule<AntiSpamType> updateModule;

    public StartHandler(IStartService startService, ApplicationContext applicationContext,
                        IModule<CalculatorType> calcModule, IModule<AntiSpamType> updateModule) {
        this.startService = startService;
        this.applicationContext = applicationContext;
        this.calcModule = calcModule;
        this.updateModule = updateModule;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        startService.process(chatId);
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.START;
    }
}
