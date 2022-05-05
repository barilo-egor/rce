package tgb.btc.rce.bean;

import lombok.Builder;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.constants.BotNumberConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.util.UpdateUtil;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private boolean isAdmin;

    public User() {
        this.step = DEFAULT_STEP;
        this.command = Command.START;
        this.registrationDate = LocalDateTime.now();
        this.isAdmin = false;
    }

    public User(Update update) {
        this.chatId = UpdateUtil.getChatId(update);
        this.username = UpdateUtil.getUsername(update);
        this.command = Command.START;
        this.registrationDate = LocalDateTime.now();
        this.step = DEFAULT_STEP;
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
}
