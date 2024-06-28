package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.UserData;
import tgb.btc.library.bean.bot.UserDiscount;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.repository.bot.UserDataRepository;
import tgb.btc.library.repository.bot.UserDiscountRepository;
import tgb.btc.library.service.bean.bot.ReferralUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserProcessService {

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private UserDataRepository userDataRepository;

    private ReferralUserService referralUserService;

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Autowired
    public void setReferralUserService(ReferralUserService referralUserService) {
        this.referralUserService = referralUserService;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    public boolean registerIfNotExists(Update update) {
        boolean isUserExists = readUserService.existsByChatId(UpdateUtil.getChatId(update));
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
                if (!readUserService.existsByChatId(chatIdFrom)) throw new BaseException();
                inviter = readUserService.getByChatId(chatIdFrom);
                newUser.setFromChatId(chatIdFrom);
            } catch (NumberFormatException | BaseException ignored) {
            }
        }
        User savedNewUser = modifyUserService.save(newUser);
        if (Objects.nonNull(inviter)) {
            inviter.getReferralUsers().add(referralUserService.save(ReferralUser.buildDefault(newUser.getChatId())));
            modifyUserService.save(inviter);
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
