package tgb.btc.rce.service.handler.impl.callback.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.web.merchant.dashpay.DashPayOrderStatus;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.web.merchant.dashpay.DashPayMerchantService;
import tgb.btc.library.vo.web.merchant.dashpay.OrderResponse;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

@Service
@Slf4j
public class DashPayPayOutHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IReadDealService readDealService;

    private final IResponseSender responseSender;

    private final DashPayMerchantService dashPayMerchantService;

    private final IModifyDealService modifyDealService;

    public DashPayPayOutHandler(ICallbackDataService callbackDataService, IReadDealService readDealService,
                                IResponseSender responseSender, DashPayMerchantService dashPayMerchantService,
                                IModifyDealService modifyDealService) {
        this.callbackDataService = callbackDataService;
        this.readDealService = readDealService;
        this.responseSender = responseSender;
        this.dashPayMerchantService = dashPayMerchantService;
        this.modifyDealService = modifyDealService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Deal deal = readDealService.findByPid(dealPid);
        String requisite = deal.getWallet();
        if (!containsOtherThanDigitsPlusSpace(requisite)) {
            responseSender.sendMessage(chatId, "Реквизит по сделке на <b>продажу №" + deal.getPid()
                    + "</b> не содержит название банка. Введите реквизиты вручную.");
            return;
        }
        try {
            OrderResponse orderResponse = dashPayMerchantService.createOrder(deal);
            deal.setMerchant(Merchant.DASH_PAY);
            deal.setMerchantOrderId(orderResponse.getData().getOrder().getId());
            deal.setMerchantOrderStatus(DashPayOrderStatus.NEW.name());
            modifyDealService.save(deal);
            responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
            responseSender.sendMessage(chatId, "Был создан ордер DashPay на вывод. " +
                    "Предыдущее сообщение заявки удалено. Для отображения нажмите \"Показать\".",
                    InlineButton.builder().text("Показать").data(callbackDataService.buildData(CallbackQueryData.SHOW_DEAL, dealPid)).build());
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Произошла ошибка при попытке создания ордера DashPay: " + e.getMessage());
            log.error("Ошибка при создании ордера на вывод: ", e);
        }
    }

    public boolean containsOtherThanDigitsPlusSpace(String str) {
        return !str.matches("[0-9+\\s]*");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DASH_PAY_PAY_OUT;
    }
}
