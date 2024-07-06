package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.interfaces.service.bean.common.bot.IDealCommonService;
import tgb.btc.rce.util.DealPromoUtil;

@Service
public class DealProcessService {

    private IDealCommonService dealCommonService;

    @Autowired
    public void setDealCommonService(IDealCommonService dealCommonService) {
        this.dealCommonService = dealCommonService;
    }

    public boolean isAvailableForPromo(Long chatId) {
        return !DealPromoUtil.isNone() && dealCommonService.isFirstDeal(chatId);
    }
}
