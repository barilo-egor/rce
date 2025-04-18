package tgb.btc.rce.service.handler.impl.state.disount;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.UserDiscount;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.service.process.PersonalDiscountsCache;
import tgb.btc.rce.enums.BotSystemMessage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.math.BigDecimal;

@Service
public class NewPersonalSellHandler implements IStateHandler {

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IResponseSender responseSender;

    private final IReadUserService readUserService;

    private final IUserDiscountService userDiscountService;

    private final PersonalDiscountsCache personalDiscountsCache;

    private final IAdminPanelService adminPanelService;

    private final IBigDecimalService bigDecimalService;

    public NewPersonalSellHandler(IRedisUserStateService redisUserStateService, IRedisStringService redisStringService,
                                  IResponseSender responseSender, IReadUserService readUserService,
                                  IUserDiscountService userDiscountService, PersonalDiscountsCache personalDiscountsCache,
                                  IAdminPanelService adminPanelService, IBigDecimalService bigDecimalService) {
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.responseSender = responseSender;
        this.readUserService = readUserService;
        this.userDiscountService = userDiscountService;
        this.personalDiscountsCache = personalDiscountsCache;
        this.adminPanelService = adminPanelService;
        this.bigDecimalService = bigDecimalService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите новый процент персональной скидки на продажу, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisUserStateService.delete(chatId);
            redisStringService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        String enteredValue = text.replace(",", ".");
        BigDecimal newPersonalSell;
        try {
            newPersonalSell = BigDecimal.valueOf(Double.parseDouble(enteredValue));
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, BotSystemMessage.ENTER_VALID_INPUT.getText());
            return;
        }
        Long userChatId = Long.parseLong(redisStringService.get(chatId));
        Long userPid = readUserService.getPidByChatId(userChatId);
        if (!userDiscountService.isExistByUserPid(userPid)) {
            UserDiscount userDiscount = new UserDiscount();
            userDiscount.setUser(new User(userPid));
            userDiscount.setPersonalSell(newPersonalSell);
            userDiscountService.save(userDiscount);
        } else userDiscountService.updatePersonalSellByUserPid(newPersonalSell, userPid);
        personalDiscountsCache.putToSell(userChatId, newPersonalSell);
        responseSender.sendMessage(chatId, "Персональная скидка на продажу для пользователя <b>" + userChatId
                + "</b> обновлена. Новое значение: <b>" + bigDecimalService.roundToPlainString(newPersonalSell, 2) + "</b>%.");
        redisStringService.delete(chatId);
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.NEW_PERSONAL_SELL;
    }
}
