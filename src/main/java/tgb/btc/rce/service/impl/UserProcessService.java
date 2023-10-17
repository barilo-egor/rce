package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.bean.UserData;
import tgb.btc.rce.bean.UserDiscount;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReferralType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.impl.bean.ReferralUserService;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserProcessService {

    private UserRepository userRepository;

    private UserDataRepository userDataRepository;

    private ReferralUserService referralUserService;

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setReferralUserService(ReferralUserService referralUserService) {
        this.referralUserService = referralUserService;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    public boolean registerIfNotExists(Update update) {
        boolean isUserExists = userRepository.existsByChatId(UpdateUtil.getChatId(update));
        if (!isUserExists) register(update);
        return isUserExists;
    }

    public User register(Update update) {
        User newUser = new User();
        newUser.setChatId(UpdateUtil.getChatId(update));
        newUser.setUsername(UpdateUtil.getUsername(update));
        newUser.setCommand(Command.START);
        newUser.setRegistrationDate(LocalDateTime.now());
        newUser.setStep(User.DEFAULT_STEP);
        newUser.setAdmin(false);
        newUser.setLotteryCount(0);
        newUser.setReferralBalance(0);
        newUser.setActive(true);
        newUser.setBanned(false);
        newUser.setCharges(0);
        newUser.setReferralPercent(BigDecimal.ZERO);
        User inviter = null;
        if (CommandUtil.isStartCommand(update) && ReferralType.STANDARD.isCurrent()) {
            try {
                Long chatIdFrom = Long.parseLong(update.getMessage().getText()
                        .replaceAll(Command.START.getText(), "").trim());
                if (!userRepository.existsByChatId(chatIdFrom)) throw new BaseException();
                inviter = userRepository.getByChatId(chatIdFrom);
                newUser.setFromChatId(chatIdFrom);
            } catch (NumberFormatException | BaseException ignored) {
            }
        }
        User savedNewUser = userRepository.save(newUser);
        if (Objects.nonNull(inviter)) {
            inviter.getReferralUsers().add(referralUserService.save(ReferralUser.buildDefault(newUser.getChatId())));
            userRepository.save(inviter);
        }
        UserDiscount userDiscount = new UserDiscount();
        userDiscount.setUser(newUser);
        userDiscount.setRankDiscountOn(true);
        userDiscountRepository.save(userDiscount);
        UserData userData = new UserData();
        userData.setUser(newUser);
        userDataRepository.save(userData);
        return savedNewUser;
    }
}
