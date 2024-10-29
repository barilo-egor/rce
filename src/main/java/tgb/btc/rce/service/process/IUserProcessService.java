package tgb.btc.rce.service.process;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;

public interface IUserProcessService {

    boolean registerIfNotExists(Update update);

    User register(Update update);


}
