package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IUserService;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Integer getStepByChatId(Long chatId) {
        return userRepository.getStepByChatId(chatId);
    }

    public Command getCommandByChatId(Long chatId) {
        return userRepository.getCommandByChatId(chatId);
    }

    @Override
    public User register(Update update) {
        User user = User.buildFromUpdate(update);
        return userRepository.save(user);
    }

    @Override
    public boolean existByChatId(Long chatId) {
        return userRepository.existsByChatId(chatId);
    }

    @Override
    public void setDefaultValues(Long chatId) {
        userRepository.setDefaultValues(chatId);
    }

    @Override
    public boolean isAdminByChatId(Long chatId) {
        return userRepository.isAdminByChatId(chatId);
    }
}
