package tgb.btc.rce.service.handler.impl.state.disount;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IUserInputService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class PersonalBuyChatIdHandler implements IStateHandler {

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final IUserDiscountService userDiscountService;

    private final IBigDecimalService bigDecimalService;

    private final ICallbackDataService callbackDataService;

    private final IUserInputService userInputService;

    public PersonalBuyChatIdHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                    IAdminPanelService adminPanelService,
                                    IUserDiscountService userDiscountService, IBigDecimalService bigDecimalService,
                                    ICallbackDataService callbackDataService, IUserInputService userInputService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
        this.userDiscountService = userDiscountService;
        this.bigDecimalService = bigDecimalService;
        this.callbackDataService = callbackDataService;
        this.userInputService = userInputService;
    }


    @Override
    public void handle(Update update) {
        if (!userInputService.hasTextInput(update)) {
            return;
        }
        Long chatId = update.getMessage().getChatId();
        Long userChatId = userInputService.getInputChatId(chatId, update.getMessage().getText());
        if (Objects.isNull(userChatId)) {
            return;
        }
        BigDecimal personalBuy = userDiscountService.getPersonalBuyByChatId(userChatId);
        if (Objects.isNull(personalBuy)) {
            personalBuy = new BigDecimal(0);
        }
        String strPersonal = bigDecimalService.roundToPlainString(personalBuy, 2);
        responseSender.sendMessage(chatId, "У пользователя <b>" + userChatId + "</b> текущая персональная скидка на покупку: <b>"
                + strPersonal + "</b>%.",
                InlineButton.builder()
                        .text("Изменить")
                        .data(callbackDataService.buildData(CallbackQueryData.PERSONAL_BUY_DISCOUNT, userChatId))
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
        return UserState.PERSONAL_BUY_CHAT_ID;
    }
}
