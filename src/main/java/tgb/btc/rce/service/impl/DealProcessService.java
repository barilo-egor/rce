package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.rce.util.DealPromoUtil;

@Service
public class DealProcessService {

    private DealService dealService;

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    public boolean isAvailableForPromo(Long chatId) {
        return !DealPromoUtil.isNone() && dealService.isFirstDeal(chatId);
    }
}
