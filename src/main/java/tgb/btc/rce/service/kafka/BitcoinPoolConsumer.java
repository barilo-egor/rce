package tgb.btc.rce.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.util.ITelegramPropertiesService;
import tgb.btc.rce.vo.PoolComplete;
import tgb.btc.rce.vo.PoolCompleteResult;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class BitcoinPoolConsumer {

    private final INotifyService notifyService;

    private final IModifyDealService modifyDealService;

    private final ITelegramPropertiesService telegramPropertiesService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public BitcoinPoolConsumer(INotifyService notifyService, IModifyDealService modifyDealService,
                               ITelegramPropertiesService telegramPropertiesService) {
        this.notifyService = notifyService;
        this.modifyDealService = modifyDealService;
        this.telegramPropertiesService = telegramPropertiesService;
    }

    @KafkaListener(topics = "pool", groupId = "rce")
    public void receive(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String key = record.key();
        if (Objects.equals(key, "update")) {
            notifyService.notifyMessage(record.value(), Set.of(UserRole.ADMIN, UserRole.OPERATOR));
        } else if (Objects.equals(key, "complete")) {
            PoolComplete poolComplete = objectMapper.readValue(record.value(), PoolComplete.class);
            Optional<PoolCompleteResult> botResult = poolComplete.getResults().stream()
                    .filter(result -> result.getBot().equals(telegramPropertiesService.getUsername()))
                    .findAny();
            if (botResult.isEmpty()) {
                return;
            }
            PoolCompleteResult poolCompleteResult = botResult.get();
            poolCompleteResult.getPids().forEach(pid -> modifyDealService.confirm(pid, poolComplete.getHash()));
        }
    }
}
