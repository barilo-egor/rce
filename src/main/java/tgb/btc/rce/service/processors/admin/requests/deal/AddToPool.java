package tgb.btc.rce.service.processors.admin.requests.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.library.service.bean.bot.deal.ReadDealService;
import tgb.btc.library.service.util.BigDecimalService;
import tgb.btc.library.vo.web.PoolDeal;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ITelegramPropertiesService;

import java.util.Optional;

@CommandProcessor(command = Command.ADD_TO_POOL)
@Slf4j
public class AddToPool extends Processor {

    private final ICryptoWithdrawalService cryptoWithdrawalService;
    private final IModifyDealService modifyDealService;
    private final ReadDealService readDealService;
    private final ITelegramPropertiesService telegramPropertiesService;
    private final BigDecimalService bigDecimalService;

    @Autowired
    public AddToPool(ICryptoWithdrawalService cryptoWithdrawalService,
                     IModifyDealService modifyDealService, ReadDealService readDealService,
                     ITelegramPropertiesService telegramPropertiesService, BigDecimalService bigDecimalService) {
        this.cryptoWithdrawalService = cryptoWithdrawalService;
        this.modifyDealService = modifyDealService;
        this.readDealService = readDealService;
        this.telegramPropertiesService = telegramPropertiesService;
        this.bigDecimalService = bigDecimalService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long dealPid = callbackQueryService.getSplitLongData(update, 1);
        Deal deal = readDealService.findByPid(dealPid);
        Optional<Message> addMessage = responseSender.sendMessage(chatId, "Добавление сделки в пул, пожалуйста подождите.");
        try {
            cryptoWithdrawalService.addPoolDeal(PoolDeal.builder()
                    .pid(dealPid)
                    .address(deal.getWallet())
                    .bot(telegramPropertiesService.getUsername())
                    .amount(bigDecimalService.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()))
                    .build());
        }  catch (ApiResponseErrorException e) {
            responseSender.sendMessage(chatId, e.getMessage());
            return;
        } finally {
            addMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        }
        modifyDealService.updateDealStatusByPid(DealStatus.AWAITING_WITHDRAWAL, dealPid);
        log.debug("Пользователь chatId={} добавил сделку {} в пул.", chatId, dealPid);
        responseSender.deleteCallbackMessageIfExists(update);
    }
}
