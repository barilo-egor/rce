package tgb.btc.rce.service.impl.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.interfaces.service.bean.common.bot.IDealCommonService;
import tgb.btc.library.service.properties.ModulesPropertiesReader;
import tgb.btc.rce.enums.FirstDealPromoType;
import tgb.btc.rce.service.process.IDealProcessService;

@Service
public class DealProcessService implements IDealProcessService {

    private IDealCommonService dealCommonService;

    private ModulesPropertiesReader modulesPropertiesReader;

    @Autowired
    public void setModulesPropertiesReader(ModulesPropertiesReader modulesPropertiesReader) {
        this.modulesPropertiesReader = modulesPropertiesReader;
    }

    @Autowired
    public void setDealCommonService(IDealCommonService dealCommonService) {
        this.dealCommonService = dealCommonService;
    }

    public boolean isAvailableForPromo(Long chatId) {
        return !(FirstDealPromoType.NONE.equals(FirstDealPromoType.valueOf(
                modulesPropertiesReader.getString("first.deal.promo.type"))))
                && dealCommonService.isFirstDeal(chatId);
    }
}
