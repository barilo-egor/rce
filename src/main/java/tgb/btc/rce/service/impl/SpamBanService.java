package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.SpamBan;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.SpamBanRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpamBanService {

    private SpamBanRepository spamBanRepository;

    private UserRepository userRepository;

    private AdminService adminService;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSpamBanRepository(SpamBanRepository spamBanRepository) {
        this.spamBanRepository = spamBanRepository;
    }

    public void saveAndNotifyAdmins(Long chatId) {
        SpamBan spamBan =
                spamBanRepository.save(new SpamBan(new User(userRepository.getPidByChatId(chatId)), LocalDateTime.now()));
        adminService.notify("Антиспам система заблокировала пользователя.",
                            KeyboardUtil.buildInline(List.of(
                                    InlineButton.builder()
                                            .text("Показать")
                                            .data(CallbackQueryUtil.buildCallbackData(
                                                    Command.SHOW_SPAM_BANNED_USER.getText(), spamBan.getPid().toString())
                                            )
                                            .build()
                            )));
    }
}
