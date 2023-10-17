package tgb.btc.rce.service.impl.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReferralType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService extends BasePersistService<User> {

    private final UserRepository userRepository;
    private final ReferralUserService referralUserService;

    private UserDiscountRepository userDiscountRepository;

    private UserDataRepository userDataRepository;

    public static final ReferralType REFERRAL_TYPE =
            ReferralType.valueOf(BotProperties.MODULES.getString("referral.type"));

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

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

    public List<Long> getChatIdsNotAdminsAndIsActiveAndNotBanned() {
        return userRepository.getChatIdsNotAdminsAndIsActiveAndNotBanned();
    }

    public void updateIsActiveByChatId(boolean isActive, Long chatId) {
        userRepository.updateIsActiveByChatId(isActive, chatId);
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

    public boolean isReferralBalanceEmpty(Long chatId) {
        Integer referralBalance = userRepository.getReferralBalanceByChatId(chatId);
        return Objects.nonNull(referralBalance) && referralBalance == 0;
    }
}
