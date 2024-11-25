package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class PersonalSellChatIdHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final IReadUserService readUserService;

    private final IUserDiscountService userDiscountService;

    private final IBigDecimalService bigDecimalService;

    private final ICallbackDataService callbackDataService;

    public PersonalSellChatIdHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                    IAdminPanelService adminPanelService, IReadUserService readUserService,
                                    IUserDiscountService userDiscountService, IBigDecimalService bigDecimalService,
                                    ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
        this.readUserService = readUserService;
        this.userDiscountService = userDiscountService;
        this.bigDecimalService = bigDecimalService;
        this.callbackDataService = callbackDataService;
    }


    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите chat id либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        long userChatId;
        try {
            userChatId = Long.parseLong(text);
        } catch (NumberFormatException e) {
            responseSender.sendMessage(chatId, "Введите валидный chat id.");
            return;
        }
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь с таким chat id не найден.");
            return;
        }
        BigDecimal personalSell = userDiscountService.getPersonalSellByChatId(userChatId);
        if (Objects.isNull(personalSell)) {
            personalSell = new BigDecimal(0);
        }
        String strPersonal = bigDecimalService.roundToPlainString(personalSell, 2);
        responseSender.sendMessage(chatId, "У пользователя <b>" + userChatId + "</b> текущая персональная скидка на продажу: <b>"
                        + strPersonal + "</b>%.",
                InlineButton.builder()
                        .text("Изменить")
                        .data(callbackDataService.buildData(CallbackQueryData.PERSONAL_SELL_DISCOUNT, userChatId))
                        .build(),
                InlineButton.builder()
                        .text("Отмена")
                        .data(CallbackQueryData.INLINE_DELETE.name())
                        .build()
        );
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.PERSONAL_SELL_CHAT_ID;
    }
}
