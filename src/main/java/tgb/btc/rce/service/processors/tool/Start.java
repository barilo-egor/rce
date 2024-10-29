package tgb.btc.rce.service.processors.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.bean.bot.BotMessageService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.Processor;

import java.util.Objects;

@CommandProcessor(command = Command.START)
@Slf4j
public class Start extends Processor {

    private BotMessageService botMessageService;

    private IReadDealService readDealService;

    private IModifyDealService modifyDealService;

    private IMessageImageResponseSender messageImageResponseSender;

    @Autowired
    public void setMessageImageResponseSender(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        run(chatId);
    }

    public void run(Long chatId) {
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
        processToMainMenu(chatId);
    }
}
