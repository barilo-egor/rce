package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.bean.bot.BotMessageService;
import tgb.btc.library.service.bean.bot.ContactService;
import tgb.btc.rce.RceApplication;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IResponseSender;

@Slf4j
public class BeanHolder {

    public static IResponseSender RESPONSE_SENDER;

    public static BotMessageService BOT_MESSAGE_SERVICE;

    public static IReadUserService READ_USER_SERVICE;

    public static ContactService CONTACTS_SERVICE;

    public static IKeyboardService KEYBOARD_SERVICE;

    public static void load() {
        log.info("Загрузка бинов в BeanHolder.");
        RESPONSE_SENDER = RceApplication.SPRING_CONTEXT.getBean(IResponseSender.class);
        BOT_MESSAGE_SERVICE = RceApplication.SPRING_CONTEXT.getBean(BotMessageService.class);
        CONTACTS_SERVICE = RceApplication.SPRING_CONTEXT.getBean(ContactService.class);
        READ_USER_SERVICE = RceApplication.SPRING_CONTEXT.getBean(IReadUserService.class);
        KEYBOARD_SERVICE = RceApplication.SPRING_CONTEXT.getBean(IKeyboardService.class);
        log.info("Загрузка бинов BeanHolder завершена.");
    }
}
