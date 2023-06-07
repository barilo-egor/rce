package tgb.btc.lib.service;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.lib.repository.ContactsRepository;
import tgb.btc.lib.repository.UserRepository;
import tgb.btc.lib.service.impl.BotMessageService;
import tgb.btc.lib.service.impl.UpdateDispatcher;

@Slf4j
public class BeanHolder {

    public static IResponseSender RESPONSE_SENDER;

    public static BotMessageService BOT_MESSAGE_SERVICE;

    public static UserRepository USER_REPOSITORY;

    public static ContactsRepository CONTACTS_REPOSITORY;

    public static void load() {
        log.info("Загрузка бинов в BeanHolder.");
        RESPONSE_SENDER = UpdateDispatcher.applicationContext.getBean(IResponseSender.class);
        BOT_MESSAGE_SERVICE = UpdateDispatcher.applicationContext.getBean(BotMessageService.class);
        USER_REPOSITORY = UpdateDispatcher.applicationContext.getBean(UserRepository.class);
        CONTACTS_REPOSITORY = UpdateDispatcher.applicationContext.getBean(ContactsRepository.class);
        log.info("Загрузка бинов BeanHolder завершена.");
    }
}
