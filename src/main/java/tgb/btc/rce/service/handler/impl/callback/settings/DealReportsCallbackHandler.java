package tgb.btc.rce.service.handler.impl.callback.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDateDealService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.ILoadReportService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DealReportsCallbackHandler implements ICallbackQueryHandler {

    public static final String TODAY = "За сегодня";

    public static final String TEN_DAYS = "За десять дней";

    public static final String MONTH = "За месяц";

    public static final String DATE = "За дату";

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final ICallbackDataService callbackDataService;

    private final IDateDealService dateDealService;

    private final IApiDealService apiDealService;

    private final ILoadReportService loadReportService;

    private final IKeyboardService keyboardService;

    public DealReportsCallbackHandler(IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                      ICallbackDataService callbackDataService, IDateDealService dateDealService,
                                      IApiDealService apiDealService, ILoadReportService loadReportService,
                                      IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.callbackDataService = callbackDataService;
        this.dateDealService = dateDealService;
        this.apiDealService = apiDealService;
        this.loadReportService = loadReportService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        String period = callbackDataService.getArgument(callbackQuery.getData(), 1);
        List<Deal> deals;
        List<ApiDeal> apiDeals;
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Optional<Message> deleteMessage = responseSender.sendMessage(chatId, "Выполняется формирование отчета. Пожалуйста подождите.");
        switch (period) {
            case TODAY:
                deals = dateDealService.getConfirmedByDateBetween(LocalDate.now());
                apiDeals = apiDealService.getAcceptedByDate(LocalDateTime.now());
                break;
            case TEN_DAYS:
                deals = dateDealService.getConfirmedByDateBetween(LocalDate.now().minusDays(10), LocalDate.now());
                apiDeals = apiDealService.getAcceptedByDateBetween(LocalDateTime.now().minusDays(10), LocalDateTime.now());
                break;
            case MONTH:
                deals = dateDealService.getConfirmedByDateBetween(LocalDate.now().minusDays(30), LocalDate.now());
                apiDeals = apiDealService.getAcceptedByDateBetween(LocalDateTime.now().minusDays(30), LocalDateTime.now());
                break;
            case DATE:
                responseSender.sendMessage(chatId, "Введите дату в формате <b>31.01.2000</b> для выгрузки отчета по сделкам.",
                        keyboardService.getReplyCancel());
                redisUserStateService.save(chatId, UserState.DATE_DEAL_REPORT);
                return;
            default:
                throw new BaseException("Не определен период, за который нужно выгрузить в отчет сделки.");
        }
        deleteMessage.ifPresent(message -> responseSender.deleteMessage(chatId, message.getMessageId()));
        loadReportService.loadReport(deals, chatId, period, apiDeals);
    }




    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DEAL_REPORTS;
    }
}
