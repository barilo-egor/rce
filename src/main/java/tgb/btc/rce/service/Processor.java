package tgb.btc.rce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Processor {
    protected IResponseSender responseSender;

    public Processor(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    public abstract void run(Update update);
}
