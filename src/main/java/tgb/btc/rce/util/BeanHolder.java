package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.bean.bot.BotMessageService;
import tgb.btc.library.service.bean.bot.ContactService;
import tgb.btc.rce.RceApplication;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;

@Slf4j
public class BeanHolder {

    public static IResponseSender responseSender;

    public static BotMessageService botMessageService;

    public static IReadUserService readUserService;

    public static ContactService contactService;

    public static IKeyboardService keyboardService;

    public static IKeyboardBuildService keyboardBuildService;

    public static void load() {
        log.info("Загрузка бинов в BeanHolder.");
        responseSender = RceApplication.SPRING_CONTEXT.getBean(IResponseSender.class);
        botMessageService = RceApplication.SPRING_CONTEXT.getBean(BotMessageService.class);
        contactService = RceApplication.SPRING_CONTEXT.getBean(ContactService.class);
        readUserService = RceApplication.SPRING_CONTEXT.getBean(IReadUserService.class);
        keyboardService = RceApplication.SPRING_CONTEXT.getBean(IKeyboardService.class);
        keyboardBuildService = RceApplication.SPRING_CONTEXT.getBean(IKeyboardBuildService.class);
        log.info("Загрузка бинов BeanHolder завершена.");
    }
}
