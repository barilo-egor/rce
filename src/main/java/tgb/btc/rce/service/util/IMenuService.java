package tgb.btc.rce.service.util;

import org.springframework.cache.annotation.Cacheable;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Menu;

public interface IMenuService {

    ReplyKeyboard build(Menu menu, UserRole userRole);

    boolean isMenuCommand(Menu menu, String text);
}
