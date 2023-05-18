package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@CommandProcessor(command = Command.START)
public class Start extends Processor {

    private BotMessageService botMessageService;

    private DealService dealService;

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        run(chatId);
    }

    public void run(Long chatId) {
        userService.updateIsActiveByChatId(true, chatId);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid)) {
            if (dealService.existByPid(currentDealPid)) {
                dealService.deleteById(currentDealPid);
            }
            userService.updateCurrentDealByChatId(null, chatId);
        }
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.START), chatId);
        processToMainMenu(chatId);
    }
}
