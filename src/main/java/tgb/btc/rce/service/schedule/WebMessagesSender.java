package tgb.btc.rce.service.schedule;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tgb.btc.postman.bot.BotMessageMailbox;
import tgb.btc.postman.bot.vo.Mail;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.KeyboardService;

import java.util.List;

@Service
public class WebMessagesSender {

    private BotMessageMailbox botMessageMailbox;

    private AdminService adminService;

    private KeyboardService keyboardService;

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setBotMessageMailbox(BotMessageMailbox botMessageMailbox) {
        this.botMessageMailbox = botMessageMailbox;
    }

    @Scheduled(fixedDelay = 2000)
    @Async
    public void sendMessages() {
        List<Mail> newMails = botMessageMailbox.receive();
        if (CollectionUtils.isEmpty(newMails)) return;
        newMails.forEach(mail -> {

        });
    }
}
