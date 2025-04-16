package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.impl.state.deal.DealHandler;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;

@Service
public class SellHandler implements ITextCommandHandler {

    private final IModifyDealService modifyDealService;

    private final DealHandler dealHandler;

    private final IRedisUserStateService redisUserStateService;

    public SellHandler(IModifyDealService modifyDealService, DealHandler dealHandler,
                       IRedisUserStateService redisUserStateService) {
        this.modifyDealService = modifyDealService;
        this.dealHandler = dealHandler;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();

        modifyDealService.createNewDeal(DealType.SELL, chatId);
        Update update = new Update();
        update.setMessage(message);
        redisUserStateService.save(chatId, UserState.DEAL);
        dealHandler.handle(update);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.SELL_BITCOIN;
    }
}
