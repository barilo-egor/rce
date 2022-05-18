package tgb.btc.rce.bean;

import lombok.Builder;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.WithdrawalRequestStatus;

import javax.persistence.*;

@Entity
@Table(name = "WITHDRAWAL_REQUEST")
@Builder
public class WithdrawalRequest extends BasePersist {
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "STATUS")
    private WithdrawalRequestStatus status;

    public WithdrawalRequest() {
    }

    public WithdrawalRequest(User user, String phoneNumber, WithdrawalRequestStatus status) {
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public static WithdrawalRequest buildFromUpdate(User user, Update update) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUser(user);
        withdrawalRequest.setStatus(WithdrawalRequestStatus.CREATED);
        withdrawalRequest.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
        return withdrawalRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WithdrawalRequestStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalRequestStatus status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
