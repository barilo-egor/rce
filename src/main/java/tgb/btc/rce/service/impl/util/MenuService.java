package tgb.btc.rce.service.impl.util;

import com.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.IMenu;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ITextCommandService;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService implements IMenuService {

    private final String CACHE_NAME = "menuService";

    private final Map<Menu, IMenu> menuMap;

    private final IKeyboardBuildService keyboardBuildService;

    private ITextCommandService commandService;

    @Autowired
    public void setCommandService(ITextCommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public MenuService(List<IMenu> menus, IKeyboardBuildService keyboardBuildService) {
        menuMap = menus.stream()
                .collect(Collectors.toMap(IMenu::getMenu, Functions.identity(),
                        (existing, replacement) -> replacement,
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
        return keyboardBuildService.buildReply(menu.getNumberOfColumns(), menu.getTextCommands().stream()
                .map(command -> ReplyButton.builder().text(commandService.getText(command)).build())
                .toList(), false);
    }
    @Cacheable(CACHE_NAME + "isMenuCommand")
    @Override
    public boolean isMenuCommand(Menu menu, String text) {
        Set<String> commands = menu.getTextCommands().stream()
                .map(commandService::getText)
                .collect(Collectors.toSet());
        for (String command : commands) {
            if (command.equals(text)) {
                return true;
            }
        }
        return false;
    }
}
