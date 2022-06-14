package tgb.btc.rce.bean;

import lombok.Builder;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.util.CommandUtil;
import tgb.btc.rce.util.UpdateUtil;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "USER")
@Builder
public class User extends BasePersist {

    public static final int DEFAULT_STEP = 0;

    @Column(name = "CHAT_ID", nullable = false, unique = true)
    private Long chatId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "STEP", columnDefinition = "int default 0")
    private Integer step;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "COMMAND", columnDefinition = "varchar(255) default 'START'")
    private Command command;

    @Column(name = "REGISTRATION_DATE", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "IS_ADMIN", nullable = false)
    private Boolean isAdmin;

    @Column(name = "LOTTERY_COUNT")
    private Integer lotteryCount;

    @Column(name = "FROM_CHAT_ID")
    private Long fromChatId;

    @Column(name = "REFERRAL_BALANCE")
    private Integer referralBalance;

    @Column(name = "CHARGES")
    private Integer charges;

    @Column(name = "BUFFER_VARIABLE", length = 3000)
    private String bufferVariable;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive;

    @Column(name = "IS_BANNED", nullable = false)
    private Boolean isBanned;

    @Column(name = "CURRENT_DEAL")
    private Long currentDeal;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ReferralUser> referralUsers;

    public User() {
    }

    public User(Long chatId, String username, Integer step, Command command, LocalDateTime registrationDate,
                Boolean isAdmin, Integer lotteryCount, Long fromChatId, Integer referralBalance, Integer charges,
                String bufferVariable, Boolean isActive, Boolean isBanned, Long currentDeal,
                List<ReferralUser> referralUsers) {
        this.chatId = chatId;
        this.username = username;
        this.step = step;
        this.command = command;
        this.registrationDate = registrationDate;
        this.isAdmin = isAdmin;
        this.lotteryCount = lotteryCount;
        this.fromChatId = fromChatId;
        this.referralBalance = referralBalance;
        this.charges = charges;
        this.bufferVariable = bufferVariable;
        this.isActive = isActive;
        this.isBanned = isBanned;
        this.currentDeal = currentDeal;
        this.referralUsers = referralUsers;
    }

    public static User buildFromUpdate(Update update) {
        User user = new User();
        user.setChatId(UpdateUtil.getChatId(update));
        user.setUsername(UpdateUtil.getUsername(update));
        user.setCommand(Command.START);
        user.setRegistrationDate(LocalDateTime.now());
        user.setStep(DEFAULT_STEP);
        user.setAdmin(false);
        user.setLotteryCount(0);
        user.setReferralBalance(0);
        user.setActive(true);
        user.setBanned(false);
        user.setCharges(0);
        return user;
    }

    private void setDefaultValues() {
        this.step = DEFAULT_STEP;
        this.command = Command.START;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime dateTime) {
        this.registrationDate = dateTime;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Integer getLotteryCount() {
        return lotteryCount;
    }

    public void setLotteryCount(Integer lotteryCount) {
        this.lotteryCount = lotteryCount;
    }

    public Long getFromChatId() {
        return fromChatId;
    }

    public void setFromChatId(Long fromChatId) {
        this.fromChatId = fromChatId;
    }

    public Integer getReferralBalance() {
        return referralBalance;
    }

    public void setReferralBalance(Integer referralBalance) {
        this.referralBalance = referralBalance;
    }

    public List<ReferralUser> getReferralUsers() {
        return referralUsers;
    }

    public void setReferralUsers(List<ReferralUser> referralUsers) {
        this.referralUsers = referralUsers;
    }

    public String getBufferVariable() {
        return bufferVariable;
    }

    public void setBufferVariable(String bufferVariable) {
        this.bufferVariable = bufferVariable;
    }


    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
    }

    public Long getCurrentDeal() {
        return currentDeal;
    }

    public void setCurrentDeal(Long currentDeal) {
        this.currentDeal = currentDeal;
    }

    public Integer getCharges() {
        return charges;
    }

    public void setCharges(Integer charges) {
        this.charges = charges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(chatId, user.chatId) && Objects.equals(username, user.username) && Objects.equals(step, user.step) && command == user.command && Objects.equals(registrationDate, user.registrationDate) && Objects.equals(isAdmin, user.isAdmin) && Objects.equals(lotteryCount, user.lotteryCount) && Objects.equals(fromChatId, user.fromChatId) && Objects.equals(referralBalance, user.referralBalance) && Objects.equals(charges, user.charges) && Objects.equals(bufferVariable, user.bufferVariable) && Objects.equals(isActive, user.isActive) && Objects.equals(isBanned, user.isBanned) && Objects.equals(currentDeal, user.currentDeal) && Objects.equals(referralUsers, user.referralUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatId, username, step, command, registrationDate, isAdmin, lotteryCount, fromChatId, referralBalance, charges, bufferVariable, isActive, isBanned, currentDeal, referralUsers);
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", username='" + username + '\'' +
                ", step=" + step +
                ", command=" + command +
                ", registrationDate=" + registrationDate +
                ", isAdmin=" + isAdmin +
                ", lotteryCount=" + lotteryCount +
                ", fromChatId=" + fromChatId +
                ", referralBalance=" + referralBalance +
                ", charges=" + charges +
                ", bufferVariable='" + bufferVariable + '\'' +
                ", isActive=" + isActive +
                ", isBanned=" + isBanned +
                ", currentDeal=" + currentDeal +
                ", referralUsers=" + referralUsers +
                '}';
    }
}
