package tgb.btc.rce.service.handler.impl.callback.discount;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.UserDiscount;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class ChangeRankDiscountHandler implements ICallbackQueryHandler{

    private final ICallbackDataService callbackDataService;

    private final IReadUserService readUserService;

    private final IUserDiscountService userDiscountService;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    public ChangeRankDiscountHandler(ICallbackDataService callbackDataService, IReadUserService readUserService,
                                     IUserDiscountService userDiscountService, IResponseSender responseSender,
                                     IKeyboardBuildService keyboardBuildService) {
        this.callbackDataService = callbackDataService;
        this.readUserService = readUserService;
        this.userDiscountService = userDiscountService;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long userChatId = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Boolean isRankDiscountOn = Boolean.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2));
        Long userPid = readUserService.getPidByChatId(userChatId);
        if (userDiscountService.isExistByUserPid(userPid)) {
            userDiscountService.updateIsRankDiscountOnByPid(isRankDiscountOn, userPid);
        } else {
            UserDiscount userDiscount = new UserDiscount();
            userDiscount.setUser(new User(userPid));
            userDiscount.setRankDiscountOn(isRankDiscountOn);
            userDiscountService.save(userDiscount);
        }
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        boolean isUserRankDiscountOn = BooleanUtils.isTrue(userDiscountService.getRankDiscountByUserChatId(userChatId));
        responseSender.sendMessage(chatId, "Пользователь chat id=" + userChatId + ".",
                keyboardBuildService.buildInline(List.of(InlineButton.builder()
                        .text(isUserRankDiscountOn ? "Выключить" : "Включить")
                        .data(callbackDataService.buildData(CallbackQueryData.CHANGE_RANK_DISCOUNT, userChatId, !isUserRankDiscountOn))
                        .build())));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.CHANGE_RANK_DISCOUNT;
    }
}
