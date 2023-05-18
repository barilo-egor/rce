package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.DISCOUNTS)
public class DiscountsProcessor extends Processor {

    @Override
    public void run(Update update) {

    }
}
