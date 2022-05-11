package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.BotVariableService;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SELL_BITCOIN)
public class SellBitcoin extends Processor {
    private final BotMessageService botMessageService;
    private final BotVariableService botVariableService;

    @Autowired
    public SellBitcoin(IResponseSender responseSender, BotMessageService botMessageService,
                       BotVariableService botVariableService) {
        super(responseSender);
        this.botMessageService = botMessageService;
        this.botVariableService = botVariableService;
    }

    @Override
    public void run(Update update) {
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.SELL_BITCOIN),
                UpdateUtil.getChatId(update), MenuFactory.getLink(BotStringConstants.WRITE_TO_OPERATOR_BUTTON_LABEL,
                        botVariableService.findByType(BotVariableType.OPERATOR_LINK).getValue()));
    }
}
