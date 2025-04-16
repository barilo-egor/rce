package tgb.btc.rce.service.handler.util;

public interface IStartService {

    void process(Long chatId);

    void processToMainMenu(Long chatId);

    void processToStartState(Long chatId);
}
