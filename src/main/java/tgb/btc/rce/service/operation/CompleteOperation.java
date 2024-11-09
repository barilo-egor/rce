package tgb.btc.rce.service.operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.service.util.ITelegramPropertiesService;
import tgb.btc.rce.vo.PoolOperation;

import java.util.Objects;

@Service
@Slf4j
public class CompleteOperation implements IPoolOperation {

    private final IModifyDealService modifyDealService;

    private final String botUsername;

    public CompleteOperation(IModifyDealService modifyDealService,
                             ITelegramPropertiesService telegramPropertiesService) {
        this.modifyDealService = modifyDealService;
        this.botUsername = telegramPropertiesService.getUsername();
    }

    @Override
    public void process(PoolOperation poolOperation) {
        if (Objects.isNull(poolOperation.getPoolDeals())) {
            log.warn("Отсутствует список сделок для операции завершения пула.");
            return;
        }
        poolOperation.getPoolDeals().forEach(deal -> {
            modifyDealService.confirm(deal.getPid(), poolOperation.getData());
            log.debug("Сделка {} подтверждена после завершения пула.", deal.getPid());
        });
    }

    @Override
    public String getOperationName() {
        return "complete";
    }
}
