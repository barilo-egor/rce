package tgb.btc.rce.service.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.bean.UserService;
import tgb.btc.rce.service.processors.Start;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class DealDeleteScheduler {

    private static final Map<Long, Integer> NEW_CRYPTO_DEALS_PIDS = new HashMap<>();

    private DealRepository dealRepository;

    private IResponseSender responseSender;

    private UserService userService;

    private Start start;

    @PostConstruct
    public void post() {
        log.info("Автоматическое удаление недействительных заявок загружено в контекст.");
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setStart(Start start) {
        this.start = start;
    }

    public static void addNewCryptoDeal(Long pid, Integer messageId) {
        synchronized (NEW_CRYPTO_DEALS_PIDS) {
            if (Objects.isNull(pid)) return;
            NEW_CRYPTO_DEALS_PIDS.put(pid, messageId);
        }
    }

    public static void deleteCryptoDeal(Long pid) {
        synchronized (NEW_CRYPTO_DEALS_PIDS) {
            NEW_CRYPTO_DEALS_PIDS.remove(pid);
        }
    }

    @Scheduled(fixedDelay = 10000)
    @Async
    public void deleteOverdueDeals() {
        if (NEW_CRYPTO_DEALS_PIDS.isEmpty()) return;
        Integer dealActiveTime = BotVariablePropertiesUtil.getInt(BotVariableType.DEAL_ACTIVE_TIME);
        Map<Long, Integer> bufferDealsPids = new HashMap<>(NEW_CRYPTO_DEALS_PIDS);
        for (Map.Entry<Long, Integer> dealData : bufferDealsPids.entrySet()) {
            Long dealPid = dealData.getKey();
            if (!dealRepository.existsById(dealPid)) deleteCryptoDeal(dealPid);
            if (isDealNotActive(dealPid, dealActiveTime)) {
                Long chatId = dealRepository.getUserChatIdByDealPid(dealPid);
                dealRepository.deleteById(dealPid);
                userService.updateCurrentDealByChatId(null, chatId);
                if (Objects.nonNull(dealData.getValue())) responseSender.deleteMessage(chatId, dealData.getValue());
                responseSender.sendMessage(chatId, String.format(MessagePropertiesUtil.getMessage("deal.deleted.auto"), dealActiveTime));
                start.run(chatId);
                deleteCryptoDeal(dealPid);
                log.debug("Автоматически удалена заявка №" + dealPid + " по истечению " + dealActiveTime + " минут.");
            }
        }
    }

    private boolean isDealNotActive(Long dealPid, Integer dealActiveTime) {
        return dealRepository.getDateTimeByPid(dealPid).plusMinutes(dealActiveTime).isBefore(LocalDateTime.now());
    }
}
