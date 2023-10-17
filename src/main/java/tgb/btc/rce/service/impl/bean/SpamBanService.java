package tgb.btc.rce.service.impl.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.SpamBan;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.repository.SpamBanRepository;
import tgb.btc.rce.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class SpamBanService {

    private SpamBanRepository spamBanRepository;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSpamBanRepository(SpamBanRepository spamBanRepository) {
        this.spamBanRepository = spamBanRepository;
    }

    public SpamBan save(Long chatId) {
        return spamBanRepository.save(new SpamBan(new User(userRepository.getPidByChatId(chatId)), LocalDateTime.now()));
    }
}
