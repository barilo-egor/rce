package tgb.btc.rce.service;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.bean.bot.BotMessageService;
import tgb.btc.library.service.bean.bot.ContactService;
import tgb.btc.rce.service.impl.UpdateDispatcher;

@Slf4j
public class BeanHolder {

    public static IResponseSender RESPONSE_SENDER;

    public static BotMessageService BOT_MESSAGE_SERVICE;

    public static IReadUserService READ_USER_SERVICE;

    public static ContactService CONTACTS_SERVICE;

    public static void load() {
        log.info("Загрузка бинов в BeanHolder.");
        RESPONSE_SENDER = UpdateDispatcher.applicationContext.getBean(IResponseSender.class);
        BOT_MESSAGE_SERVICE = UpdateDispatcher.applicationContext.getBean(BotMessageService.class);
        CONTACTS_SERVICE = UpdateDispatcher.applicationContext.getBean(ContactService.class);
        READ_USER_SERVICE = UpdateDispatcher.applicationContext.getBean(IReadUserService.class);
        log.info("Загрузка бинов BeanHolder завершена.");
    }
}
