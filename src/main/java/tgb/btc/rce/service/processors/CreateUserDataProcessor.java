package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.UserData;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.repository.bot.UserDataRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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
