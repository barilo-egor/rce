package tgb.btc.rce.service.process;

public interface ISendLogsService {
    void send(Long chatId, boolean isArchive);
}
