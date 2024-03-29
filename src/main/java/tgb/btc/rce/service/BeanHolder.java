package tgb.btc.rce.service;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.repository.bot.ContactRepository;
import tgb.btc.library.repository.bot.UserRepository;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.UpdateDispatcher;

@Slf4j
public class BeanHolder {

    public static IResponseSender RESPONSE_SENDER;

    public static BotMessageService BOT_MESSAGE_SERVICE;

    public static UserRepository USER_REPOSITORY;

    public static ContactRepository CONTACTS_REPOSITORY;

    public static void load() {
        log.info("Загрузка бинов в BeanHolder.");
        RESPONSE_SENDER = UpdateDispatcher.applicationContext.getBean(IResponseSender.class);
        BOT_MESSAGE_SERVICE = UpdateDispatcher.applicationContext.getBean(BotMessageService.class);
        USER_REPOSITORY = UpdateDispatcher.applicationContext.getBean(UserRepository.class);
        CONTACTS_REPOSITORY = UpdateDispatcher.applicationContext.getBean(ContactRepository.class);
        log.info("Загрузка бинов BeanHolder завершена.");
    }
}
