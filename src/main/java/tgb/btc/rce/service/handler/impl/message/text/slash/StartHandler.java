package tgb.btc.rce.service.handler.impl.message.text.slash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

import java.util.Objects;

@Service
@Slf4j
public class StartHandler implements ISlashCommandHandler {

    private final IResponseSender responseSender;

    private final IReadDealService readDealService;

    private final IModifyDealService modifyDealService;

    private final IMessageImageResponseSender messageImageResponseSender;

    private final IModifyUserService modifyUserService;

    private final IReadUserService readUserService;

    private final IMessagePropertiesService messagePropertiesService;

    private final IMenuService menuService;

    public StartHandler(IResponseSender responseSender, IReadDealService readDealService,
                        IModifyDealService modifyDealService, IMessageImageResponseSender messageImageResponseSender,
                        IModifyUserService modifyUserService, IReadUserService readUserService,
                        IMessagePropertiesService messagePropertiesService, IMenuService menuService) {
        this.responseSender = responseSender;
        this.readDealService = readDealService;
        this.modifyDealService = modifyDealService;
        this.messageImageResponseSender = messageImageResponseSender;
        this.modifyUserService = modifyUserService;
        this.readUserService = readUserService;
        this.messagePropertiesService = messagePropertiesService;
        this.menuService = menuService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        modifyUserService.updateIsActiveByChatId(true, chatId);
        messageImageResponseSender.sendMessage(MessageImage.START, chatId);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid)) {
            if (readDealService.existsById(currentDealPid)) {
                log.info("Сделка " + currentDealPid + " удалена по команде /start");
                modifyDealService.deleteById(currentDealPid);
            }
            modifyUserService.updateCurrentDealByChatId(null, chatId);
        }
        modifyUserService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId,
                messagePropertiesService.getMessage(PropertiesMessage.MENU_MAIN),
                menuService.build(Menu.MAIN, readUserService.getUserRoleByChatId(chatId)), "HTML");
    }

    @Override
    public String getSlashCommand() {
        return SlashCommand.START.getText();
    }
}
