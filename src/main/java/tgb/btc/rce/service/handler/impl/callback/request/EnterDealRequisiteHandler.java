package tgb.btc.rce.service.handler.impl.callback.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.rce.enums.RedisPrefix;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class EnterDealRequisiteHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IReadDealService readDealService;

    private final IRedisUserStateService redisUserStateService;

    private final IRedisStringService redisStringService;

    private final IKeyboardService keyboardService;

    public EnterDealRequisiteHandler(IResponseSender responseSender,
                                     ICallbackDataService callbackDataService, IReadDealService readDealService,
                                     IRedisUserStateService redisUserStateService, IRedisStringService redisStringService,
                                     IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.readDealService = readDealService;
        this.redisUserStateService = redisUserStateService;
        this.redisStringService = redisStringService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Deal deal = readDealService.findByPid(dealPid);
        responseSender.sendMessage(chatId, "Текущее значение реквизита сделки <b>№" + dealPid + "</b>:\n" +
                "<code>" + deal.getWallet() + "</code>\nВведите новое значение.", keyboardService.getReplyCancel());
        redisUserStateService.save(chatId, UserState.ENTER_DEAL_REQUISITE);
        redisStringService.save(RedisPrefix.DEAL_PID, chatId, dealPid.toString());
        redisStringService.save(RedisPrefix.MESSAGE_ID, chatId, String.valueOf(callbackQuery.getMessage().getMessageId()));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ENTER_DEAL_REQUISITE;
    }

}
