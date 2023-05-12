package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.*;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_USER)
public class DeleteUser extends Processor {

    private UserRepository userRepository;

    private DealRepository dealRepository;

    private UserDiscountRepository userDiscountRepository;

    private UserDataRepository userDataRepository;

    private ReferralUserRepository referralUserRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public DeleteUser(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    @Transactional
    public void run(Update update) {
        try {
            Long chatId = Long.parseLong(UpdateUtil.getMessageText(update).split(" ")[1]);
            Long userPid = userRepository.getPidByChatId(chatId);

            responseSender.sendMessage(chatId, "Пользователь " + chatId + " удален.");
        } catch (Exception e) {
            responseSender.sendMessage(UpdateUtil.getChatId(update), "Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

}
