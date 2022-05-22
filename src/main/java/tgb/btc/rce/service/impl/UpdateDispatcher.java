package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CommandProcessorLoader;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

@Service
public class UpdateDispatcher implements IUpdateDispatcher {

    public static ApplicationContext applicationContext;

    private final UserService userService;

    @Autowired
    public UpdateDispatcher(UserService userService) {
        this.userService = userService;
    }

    public void dispatch(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (userService.getIsBannedByChatId(chatId)) return;
        if (!userService.existByChatId(chatId)) userService.register(update);
        ((Processor) applicationContext.getBean(CommandProcessorLoader.getByCommand(getCommand(update)))).run(update);
    }

    private Command getCommand(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Command command = userService.getStepByChatId(chatId).equals(User.DEFAULT_STEP) || CommandUtil.isStartCommand(update) ?
                Command.fromUpdate(update) : userService.getCommandByChatId(chatId);
        if (!hasAccess(command, chatId)) return Command.START;
        else return command;
    }

    private boolean hasAccess(Command command, Long chatId) {
        /* TODO Егор: Если команда является админ командой, а юзер по этому чат айди не админ, то доступа нет. Во всех
            остальных случаях доступ есть.
            Здесь тебе чисто потренировать чтение чужого кода, изучи классы которые в этом классе есть.
            Если долго не будет получаться, то я тебе дам подсказки.
         */
        return true;
    }


}
