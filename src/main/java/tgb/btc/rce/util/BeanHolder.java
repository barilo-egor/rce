package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.service.bean.bot.ContactService;
import tgb.btc.rce.RceApplication;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IResponseSender;

@Slf4j
public class BeanHolder {

    public static IResponseSender RESPONSE_SENDER = RceApplication.SPRING_CONTEXT.getBean(IResponseSender.class);

    public static ContactService CONTACTS_SERVICE = RceApplication.SPRING_CONTEXT.getBean(ContactService.class);

    public static IKeyboardService KEYBOARD_SERVICE = RceApplication.SPRING_CONTEXT.getBean(IKeyboardService.class);
}
