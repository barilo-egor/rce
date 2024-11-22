package tgb.btc.rce.service.handler.util;

import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.web.api.ApiDeal;

import java.util.List;

public interface ILoadReportService {
    void loadReport(List<Deal> deals, Long chatId, String period, List<ApiDeal> apiDeals);
}
