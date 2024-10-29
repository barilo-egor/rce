package tgb.btc.rce.service;

import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

// TODO добавить @Cacheable на build
public interface IMenu {

    Menu getMenu();

    List<ReplyButton> build(UserRole userRole);

    boolean isOneTime();
}
