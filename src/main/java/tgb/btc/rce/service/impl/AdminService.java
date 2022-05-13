package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserService userService;
    private final ResponseSender responseSender;

    @Autowired
    public AdminService(UserService userService, ResponseSender responseSender) {
        this.userService = userService;
        this.responseSender = responseSender;
    }

    public void notify(String message) {
        userService.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message));
    }
}
