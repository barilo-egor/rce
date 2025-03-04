package tgb.btc.rce.service.handler.util.impl.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.IMenuService;

import java.util.Objects;

@Service
@Slf4j
public class StartService implements IStartService {

    private final IReadDealService readDealService;

    private final IModifyDealService modifyDealService;

    private final IMessageImageResponseSender messageImageResponseSender;

    private final IModifyUserService modifyUserService;

    private final IReadUserService readUserService;

    private final IMenuService menuService;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    public StartService(IReadDealService readDealService,
                        IModifyDealService modifyDealService, IMessageImageResponseSender messageImageResponseSender,
                        IModifyUserService modifyUserService, IReadUserService readUserService,IMenuService menuService,
                        IRedisUserStateService redisUserStateService, IRedisStringService redisStringService) {
        this.readDealService = readDealService;
        this.modifyDealService = modifyDealService;
        this.messageImageResponseSender = messageImageResponseSender;
        this.modifyUserService = modifyUserService;
        this.readUserService = readUserService;
        this.menuService = menuService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
    }

    @Override
    public void process(Long chatId) {
        modifyUserService.updateIsActiveByChatId(true, chatId);
        messageImageResponseSender.sendMessage(MessageImage.START, chatId);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid)) {
            if (readDealService.existsById(currentDealPid)) {
                log.info("Сделка {} удалена по команде /start", currentDealPid);
                modifyDealService.deleteById(currentDealPid);
            }
            modifyUserService.updateCurrentDealByChatId(null, chatId);
        }
        modifyUserService.setDefaultValues(chatId);
        redisUserStateService.delete(chatId);
        redisStringService.delete(chatId);
        messageImageResponseSender.sendMessage(MessageImage.MAIN_MENU, chatId,
                menuService.build(Menu.MAIN, readUserService.getUserRoleByChatId(chatId)));
    }

    @Override
    public void processToMainMenu(Long chatId) {
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        if (Objects.nonNull(currentDealPid)) {
            if (readDealService.existsById(currentDealPid)) {
                modifyDealService.deleteById(currentDealPid);
            }
            modifyUserService.updateCurrentDealByChatId(null, chatId);
        }
        redisUserStateService.delete(chatId);
        redisStringService.deleteAll(chatId);
        modifyUserService.setDefaultValues(chatId);
        messageImageResponseSender.sendMessage(MessageImage.MAIN_MENU, chatId,
                menuService.build(Menu.MAIN, readUserService.getUserRoleByChatId(chatId)));
    }
}
