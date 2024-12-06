package tgb.btc.rce.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.operation.IPoolOperation;
import tgb.btc.rce.vo.PoolOperation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BitcoinPoolConsumer {

    private final INotifyService notifyService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, IPoolOperation> poolOperationsMap;

    private final String botUsername;

    @Autowired
    public BitcoinPoolConsumer(List<IPoolOperation> poolOperations, INotifyService notifyService,
                               @Value("${bot.username}") String botUsername) {
        this.notifyService = notifyService;
        this.poolOperationsMap = poolOperations.stream()
                .collect(Collectors.toMap(IPoolOperation::getOperationName, operation -> operation));
        this.botUsername = botUsername;
    }

    @KafkaListener(topics = "pool", groupId = "${bot.username}")
    public void receive(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String key = record.key();
        if ("operation".equals(key)) {
            PoolOperation poolOperation = objectMapper.readValue(record.value(), PoolOperation.class);
            poolOperation.getPoolDeals().removeIf(poolDeal -> !botUsername.equals(poolDeal.getBot()));
            poolOperationsMap.get(poolOperation.getOperation()).process(poolOperation);
        } else if ("message".equals(key)) {
            processMessage(record.value());
        }
    }

    private void processMessage(String message) {
        notifyService.notifyMessage(message, Set.of(UserRole.OPERATOR, UserRole.ADMIN));
    }
}
