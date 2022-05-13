package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;
import java.util.Objects;

@Service
public class UserService extends BasePersistService<User> {

    private final UserRepository userRepository;

    @Autowired
    public UserService(BaseRepository<User> baseRepository, UserRepository userRepository) {
        super(baseRepository);
        this.userRepository = userRepository;
    }

    public Integer getStepByChatId(Long chatId) {
        return userRepository.getStepByChatId(chatId);
    }

    public Command getCommandByChatId(Long chatId) {
        return userRepository.getCommandByChatId(chatId);
    }

    public User register(Update update) {
        User newUser = User.buildFromUpdate(update);
        User inviter = null;
        if (CommandUtil.isStartCommand(update)) {
            try {
                Long chatIdFrom = Long.parseLong(update.getMessage().getText()
                        .replaceAll(Command.START.getText(), ""));
                if (!existByChatId(chatIdFrom)) throw new BaseException();
                inviter = userRepository.getByChatId(chatIdFrom);
                newUser.setFromChatId(chatIdFrom);
            } catch (NumberFormatException | BaseException ignored) {
            }
        }
        User savedNewUser = userRepository.save(newUser);
        if (Objects.nonNull(inviter)) {
            inviter.getReferralUsers().add(ReferralUser.buildDefault(newUser.getChatId()));
            userRepository.save(inviter);
        }
        return savedNewUser;
    }

    public boolean existByChatId(Long chatId) {
        return userRepository.existsByChatId(chatId);
    }

    public void setDefaultValues(Long chatId) {
        userRepository.setDefaultValues(chatId);
    }

    public boolean isAdminByChatId(Long chatId) {
        return userRepository.isAdminByChatId(chatId);
    }

    public User findByChatId(Update update) {
        return userRepository.findByChatId(UpdateUtil.getChatId(update));
    }

    public Integer getReferralBalanceByChatId(Long chatId) {
        return userRepository.getReferralBalanceByChatId(chatId);
    }

    public List<ReferralUser> getUserReferralsByChatId(@Param("chatId") Long chatId) {
        return userRepository.getUserReferralsByChatId(chatId);
    }

    public void nextStep(Long chatId) {
        userRepository.nextStep(chatId);
    }

    public List<Long> getAdminsChatIds() {
        return userRepository.getAdminsChatIds();
    }
}
