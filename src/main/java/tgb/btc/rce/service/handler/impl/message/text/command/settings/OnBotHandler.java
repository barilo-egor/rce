package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IBotSwitch;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;

@Service
public class OnBotHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IModifyUserService modifyUserService;

    private final IBotSwitch botSwitch;

    private final IAdminPanelService adminPanelService;

    public OnBotHandler(IResponseSender responseSender, IModifyUserService modifyUserService, IBotSwitch botSwitch,
                        IAdminPanelService adminPanelService) {
        this.responseSender = responseSender;
        this.modifyUserService = modifyUserService;
        this.botSwitch = botSwitch;
        this.adminPanelService = adminPanelService;
    }

    @Override
    public void handle(Message message) {
        botSwitch.setOn(true);
        Long chatId = message.getChatId();
        responseSender.sendMessage(chatId, "Бот включен.");
        modifyUserService.setDefaultValues(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ON_BOT;
    }
}
