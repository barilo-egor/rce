package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.UpdateRunnable;
import tgb.btc.rce.service.impl.UserService;

@Service
public class SimpleProcessor implements UpdateRunnable {
    @Autowired
    protected IResponseSender responseSender;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;

    @Override
    public void run(Update update) {

    }
}
