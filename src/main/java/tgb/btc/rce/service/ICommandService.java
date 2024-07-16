package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ICommandService {


    boolean isStartCommand(Update update);

    boolean isSubmitCommand(Update update);
}
