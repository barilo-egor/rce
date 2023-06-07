package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.User;
import tgb.btc.lib.bean.UserData;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.UserDataRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.util.List;

@CommandProcessor(command = Command.CREATE_USER_DATA)
public class CreateUserDataProcessor extends Processor {

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    @Async
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, "Начало создания UserData.");
        List<Long> userPids = userRepository.getPids();
        for (Long userPid : userPids) {
            if (userDataRepository.countByUserPid(userPid) < 1) {
                UserData userData = new UserData();
                userData.setUser(new User(userPid));
                userDataRepository.save(userData);
            }
        }
        responseSender.sendMessage(chatId, "Конец создания UserData.");
    }

}
