package tgb.btc.rce.service.handler.impl.state.report;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.library.service.bean.bot.deal.read.DateDealService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.ILoadReportService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class IDateDealReportHandler implements IStateHandler {

    private final ResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    private final ILoadReportService loadReportService;

    private final DateDealService dateDealService;

    private final IApiDealService apiDealService;

    public IDateDealReportHandler(ResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                  IAdminPanelService adminPanelService, ILoadReportService loadReportService,
                                  DateDealService dateDealService, IApiDealService apiDealService) {
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
        this.loadReportService = loadReportService;
        this.dateDealService = dateDealService;
        this.apiDealService = apiDealService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите дату для выгрузки отчета, либо нажмите \"" + TextCommand.CANCEL + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();
        if (TextCommand.CANCEL.getText().equals(text)) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        LocalDate date;
        try {
            date = getDate(text);
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка при проверке валидности даты. Проверьте введенную дату.");
            return;
        }
        LocalDateTime dateTime = date.atStartOfDay();
        loadReportService.loadReport(dateDealService.getConfirmedByDateBetween(date), chatId,
                date.format(DateTimeFormatter.ISO_DATE), apiDealService.getAcceptedByDate(dateTime));
    }

    private LocalDate getDate(String text) {
        String[] values = text.split("\\.");
        try {
            if (values.length != 3) throw new BaseException("Неверный формат даты.");
            return LocalDate.of(Integer.parseInt(values[2]), Integer.parseInt(values[1]), Integer.parseInt(values[0]));
        } catch (DateTimeException e) {
            throw new BaseException("Неверный формат даты.");
        }
    }

    @Override
    public UserState getUserState() {
        return UserState.DATE_DEAL_REPORT;
    }
}
