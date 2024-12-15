package tgb.btc.rce.service.operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.vo.PoolOperation;

import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class DeleteOperation implements IPoolOperation {

    private final IModifyDealService modifyDealService;

    private final INotifyService notifyService;

    public DeleteOperation(IModifyDealService modifyDealService, INotifyService notifyService) {
        this.modifyDealService = modifyDealService;
        this.notifyService = notifyService;
    }

    @Override
    public void process(PoolOperation poolOperation) {
        if (Objects.isNull(poolOperation.getPoolDeals())) {
            log.warn("Отсутствуют список сделок после удаления сделки из пула.");
            return;
        }
        poolOperation.getPoolDeals().forEach(deal -> {
            modifyDealService.updateDealStatusByPid(DealStatus.PAID, deal.getPid());
            log.debug("Сделка {} возвращена в активные после удаления из пула.", deal.getPid());
            notifyService.notifyMessage("Сделка №" + deal.getPid() +
                            " возвращена в список активных заявок после удаления из пула.",
                    Set.of(UserRole.OPERATOR, UserRole.ADMIN));
        });
    }

    @Override
    public String getOperationName() {
        return "delete";
    }
}
