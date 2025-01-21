package tgb.btc.rce.service.handler.impl.callback.request.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.library.service.bean.bot.deal.ReadDealService;
import tgb.btc.library.service.util.BigDecimalService;
import tgb.btc.library.vo.web.PoolDeal;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class AddToPoolHandler implements ICallbackQueryHandler {

    private final ICryptoWithdrawalService cryptoWithdrawalService;

    private final IModifyDealService modifyDealService;

    private final ReadDealService readDealService;

    private final BigDecimalService bigDecimalService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final String botUsername;

    public AddToPoolHandler(ICryptoWithdrawalService cryptoWithdrawalService, IModifyDealService modifyDealService,
                            ReadDealService readDealService, BigDecimalService bigDecimalService,
                            IResponseSender responseSender, ICallbackDataService callbackDataService,
                            @Value("${bot.username}") String botUsername) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.modifyDealService = modifyDealService;
        this.readDealService = readDealService;
        this.bigDecimalService = bigDecimalService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.botUsername = botUsername;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Deal deal = readDealService.findByPid(dealPid);
        Optional<Message> addMessage = responseSender.sendMessage(chatId, "Добавление сделки в пул, пожалуйста подождите.");
        try {
            cryptoWithdrawalService.addPoolDeal(PoolDeal.builder()
                    .pid(dealPid)
                    .address(deal.getWallet())
                    .bot(botUsername)
                    .amount(bigDecimalService.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()))
                    .addDate(LocalDateTime.now())
                    .deliveryType(deal.getDeliveryType())
                    .build());
            modifyDealService.updateDealStatusByPid(DealStatus.AWAITING_WITHDRAWAL, dealPid);
            log.debug("Пользователь chatId={} добавил сделку {} в пул.", chatId, dealPid);
            responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        }  catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
        } finally {
            addMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ADD_TO_POOL;
    }
}
