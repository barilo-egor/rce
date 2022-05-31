package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.ResponseSender;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ExchangeService {

    private static final List<InlineButton> currencies = new ArrayList<>();

    private final ResponseSender responseSender;
    private final UserService userService;
    private final DealService dealService;

    @Autowired
    public ExchangeService(ResponseSender responseSender, UserService userService, DealService dealService) {
        this.responseSender = responseSender;
        this.userService = userService;
        this.dealService = dealService;
    }

    static {
        Arrays.asList(CryptoCurrency.values())
                .forEach(currency -> currencies.add(InlineButton.builder()
                        .text(currency.getDisplayName())
                        .data(currency.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build()));
    }

    public void createDeal(Long chatId) {
        Deal deal = new Deal();
        deal.setActive(false);
        deal.setPassed(false);
        deal.setUser(userService.findByChatId(chatId));
        Deal savedDeal = dealService.save(deal);
        userService.updateCurrentDealByChatId(savedDeal.getPid(), chatId);
    }

    public void askForCurrency(Long chatId) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.CHOOSE_CURRENCY),
                KeyboardUtil.buildInline(currencies));
        userService.nextStep(chatId, Command.BUY_BITCOIN);
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }


    public void askForSum(Long chatId) {
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM),
                dealService.getCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId))));
        userService.nextStep(chatId);
        optionalMessage.ifPresent(message ->
                userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }

    public void validateSum(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            throw new BaseException("Не введена сумма.");
        Integer sum = UpdateUtil.getIntFromText(update);

    }
}
