package tgb.btc.rce.service.impl.processors.role;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@Slf4j
@CommandProcessor(command = Command.MAKE_ADMIN)
public class MakeOperator extends Processor {


    @Override
    public void run(Update update) {

    }
}
