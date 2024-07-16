package tgb.btc.rce.service.impl;

import com.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.IMenuService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuService implements IMenuService {

    private final Map<Menu, IMenu> menuMap;

    private final IKeyboardBuildService keyboardBuildService;

    @Autowired
    public MenuService(List<IMenu> menus, IKeyboardBuildService keyboardBuildService) {
        menuMap = menus.stream()
                .collect(Collectors.toMap(IMenu::getMenu, Functions.identity(),
                        (existing, replacement) -> existing,
                        () -> new EnumMap<>(Menu.class)));
        this.keyboardBuildService = keyboardBuildService;
    }

    public Map<Menu, IMenu> getMenuMap() {
        return new EnumMap<>(menuMap);
    }

    @Override
    public ReplyKeyboard build(Menu menu, UserRole userRole) {
        IMenu iMenu = menuMap.get(menu);
        if (Objects.nonNull(iMenu))
            return keyboardBuildService.buildReply(menu.getNumberOfColumns(), iMenu.build(userRole), iMenu.isOneTime());
        return keyboardBuildService.buildReply(menu.getNumberOfColumns(), menu.getCommands().stream()
                .map(command -> ReplyButton.builder().text(command.getText()).build())
                .collect(Collectors.toList()), false);
    }
}
