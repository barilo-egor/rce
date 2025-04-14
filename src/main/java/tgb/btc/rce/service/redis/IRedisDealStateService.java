package tgb.btc.rce.service.redis;

import tgb.btc.rce.enums.DealState;

public interface IRedisDealStateService {

    void save(Long chatId, DealState state);

    DealState get(Long key);

    void delete(Long key);
}
