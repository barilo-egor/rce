package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.lib.bean.SpamBan;
import tgb.btc.lib.bean.User;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.SpamBanRepository;
import tgb.btc.lib.repository.UserRepository;
import tgb.btc.lib.util.CallbackQueryUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.vo.InlineButton;

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
