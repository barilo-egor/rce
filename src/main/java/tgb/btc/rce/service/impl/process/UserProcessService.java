package tgb.btc.rce.service.impl.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.UserData;
import tgb.btc.library.bean.bot.UserDiscount;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.service.bean.bot.IReferralUserService;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.bean.bot.ReferralUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.process.IUserProcessService;
import tgb.btc.rce.service.util.ICommandService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserProcessService implements IUserProcessService {

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private IUserDataService userDataService;

    private IReferralUserService referralUserService;

    private IUserDiscountService userDiscountService;

    private ICommandService commandService;

    private IUpdateService updateService;

    private IModule<ReferralType> referralModule;

    @Autowired
    public void setReferralModule(IModule<ReferralType> referralModule) {
        this.referralModule = referralModule;
    }

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setReferralUserService(IReferralUserService referralUserService) {
        this.referralUserService = referralUserService;
    }

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public void setUserDiscountService(IUserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

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

    public boolean registerIfNotExists(Update update) {
        boolean isUserExists = readUserService.existsByChatId(updateService.getChatId(update));
        if (!isUserExists) register(update);
        return isUserExists;
    }

    public User register(Update update) {
        User newUser = new User();
        newUser.setChatId(updateService.getChatId(update));
        newUser.setUsername(updateService.getUsername(update));
        newUser.setCommand(Command.START);
        newUser.setRegistrationDate(LocalDateTime.now());
        newUser.setStep(User.DEFAULT_STEP);
        newUser.setUserRole(UserRole.USER);
        newUser.setLotteryCount(0);
        newUser.setReferralBalance(0);
        newUser.setActive(true);
        newUser.setBanned(false);
        newUser.setCharges(0);
        newUser.setReferralPercent(BigDecimal.ZERO);
        User inviter = null;
        if (commandService.isStartCommand(update) && referralModule.isCurrent(ReferralType.STANDARD)) {
            try {
                Long chatIdFrom = Long.parseLong(update.getMessage().getText()
                        .replaceAll(commandService.getText(Command.START), "").trim());
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
        userDiscountService.save(userDiscount);
        UserData userData = new UserData();
        userData.setUser(newUser);
        userDataService.save(userData);
        return savedNewUser;
    }
}
