package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

import java.util.Objects;

@CommandProcessor(command = Command.START)
public class Start extends Processor {

    private final BotMessageService botMessageService;
    private final DealService dealService;

    @Autowired
    public Start(IResponseSender responseSender, UserService userService, BotMessageService botMessageService,
                 DealService dealService) {
        super(responseSender, userService);
        this.botMessageService = botMessageService;
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userService.updateIsActiveByChatId(true, chatId);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid) && dealService.existByPid(currentDealPid)) {
            dealService.deleteById(currentDealPid);
        }
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.START), chatId);
        processToMainMenu(chatId);
    }
}
