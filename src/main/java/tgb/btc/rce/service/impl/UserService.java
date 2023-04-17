package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.bean.UserDiscount;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService extends BasePersistService<User> {

    private final UserRepository userRepository;
    private final ReferralUserService referralUserService;

    private UserDiscountRepository userDiscountRepository;

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public UserService(BaseRepository<User> baseRepository, UserRepository userRepository,
                       ReferralUserService referralUserService) {
        super(baseRepository);
        this.userRepository = userRepository;
        this.referralUserService = referralUserService;
    }

    public Integer getStepByChatId(Long chatId) {
        return userRepository.getStepByChatId(chatId);
    }

    public boolean isDefaultStep(Long chatId) {
        return User.DEFAULT_STEP == getStepByChatId(chatId);
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
                        .replaceAll(Command.START.getText(), "").trim());
                if (!existByChatId(chatIdFrom)) throw new BaseException();
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

    public User findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public Integer getReferralBalanceByChatId(Long chatId) {
        return userRepository.getReferralBalanceByChatId(chatId);
    }

    public List<ReferralUser> getUserReferralsByChatId(@Param("chatId") Long chatId) {
        return userRepository.getUserReferralsByChatId(chatId);
    }

    public void nextStep(Long chatId, Command command) {
        userRepository.nextStep(chatId, command);
    }

    public void nextStep(Long chatId) {
        userRepository.nextStep(chatId);
    }

    public void previousStep(Long chatId) {
        userRepository.previousStep(chatId);
    }

    public List<Long> getAdminsChatIds() {
        return userRepository.getAdminsChatIds();
    }

    @Override
    public User save(User user) {
        if (user.getReferralBalance() < 0)
            throw new BaseException("Сохранения пользователя невозможно, т.к. реферальный баланс отрицателен. " + user);
        return userRepository.save(user);
    }

    public void updateBufferVariable(Long chatId, String bufferVariable) {
        userRepository.updateBufferVariable(chatId, bufferVariable);
    }

    public String getBufferVariable(Long chatId) {
        return userRepository.getBufferVariable(chatId);
    }

    public List<Long> getChatIdsNotAdmins() {
        return userRepository.getChatIdsNotAdmins();
    }

    public void updateIsActiveByChatId(boolean isActive, Long chatId) {
        userRepository.updateIsActiveByChatId(isActive, chatId);
    }

    public Boolean getIsBannedByChatId(Long chatId) {
        return userRepository.getIsBannedByChatId(chatId);
    }

    public void updateCurrentDealByChatId(Long dealPid, Long chatId) {
        userRepository.updateCurrentDealByChatId(dealPid, chatId);
    }

    public Long getCurrentDealByChatId(Long chatId) {
        return userRepository.getCurrentDealByChatId(chatId);
    }

    public String getUsernameByChatId(Long chatId) {
        return userRepository.getUsernameByChatId(chatId);
    }

    public void updateCommandByChatId(Command command, Long chatId) {
        userRepository.updateCommandByChatId(command, chatId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void updateReferralBalanceByChatId(Integer referralBalance, Long chatId) {
        userRepository.updateReferralBalanceByChatId(referralBalance, chatId);
    }

    public void updateChargesByChatId(Integer charges, Long chatId) {
        userRepository.updateChargesByChatId(charges, chatId);
    }

    public Integer getChargesByChatId(Long chatId) {
        return userRepository.getChargesByChatId(chatId);
    }
}
