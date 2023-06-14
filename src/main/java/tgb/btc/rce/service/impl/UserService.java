package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.bean.UserData;
import tgb.btc.rce.bean.UserDiscount;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.ReferralType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.*;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.report.ReportUserVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserService extends BasePersistService<User> {

    private final UserRepository userRepository;
    private final ReferralUserService referralUserService;

    private UserDiscountRepository userDiscountRepository;

    private UserDataRepository userDataRepository;

    private BannedUserCache bannedUserCache;

    private DealRepository dealRepository;

    public static final ReferralType REFERRAL_TYPE =
            ReferralType.valueOf(BotProperties.MODULES.getString("referral.type"));

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setBannedUserCache(BannedUserCache bannedUserCache) {
        this.bannedUserCache = bannedUserCache;
    }

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

    /**
     * Регистрирует пользователя, если он не существует в базе.
     *
     * @return существовал ли пользователь ДО регистрации.
     */
    public boolean registerIfNotExists(Update update) {
        boolean isUserExists = existByChatId(UpdateUtil.getChatId(update));
        if (!isUserExists) register(update);
        return isUserExists;
    }

    public User register(Update update) {
        User newUser = User.buildFromUpdate(update);
        User inviter = null;
        if (CommandUtil.isStartCommand(update) && ReferralType.STANDARD.equals(REFERRAL_TYPE)) {
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
        UserData userData = new UserData();
        userData.setUser(newUser);
        userDataRepository.save(userData);
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

    public void ban(Long chatId) {
        bannedUserCache.remove(chatId);
        userRepository.updateIsBannedByChatId(chatId, true);
    }

    public void unban(Long chatId) {
        bannedUserCache.remove(chatId);
        userRepository.updateIsBannedByChatId(chatId, false);
    }

    public boolean isReferralBalanceEmpty(Long chatId) {
        Integer referralBalance = userRepository.getReferralBalanceByChatId(chatId);
        return Objects.nonNull(referralBalance) && referralBalance == 0;
    }

    public List<ReportUserVO> findAllForUsersReport() {
        List<Object[]> raws = userRepository.findAllForUsersReport();
        List<ReportUserVO> users = new ArrayList<>();
        for (Object[] raw : raws) {
            users.add(ReportUserVO.builder()
                    .pid((Long) raw[0])
                    .chatId((Long) raw[1])
                    .username((String) raw[2])
                    .build());
        }
        return users;
    }
}
