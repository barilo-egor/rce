package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.USERS_DEALS_REPORT)
@Slf4j
public class UsersDealsReport extends Processor {

    public UsersDealsReport(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Тут нихуя нет");
    }
}
