package tgb.btc.lib.bean;

import lombok.Builder;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.enums.WithdrawalRequestStatus;

import javax.persistence.*;
import java.util.Objects;

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

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    public WithdrawalRequest() {
    }

    public WithdrawalRequest(User user, String phoneNumber, WithdrawalRequestStatus status, Boolean isActive) {
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.isActive = isActive;
    }

    public static WithdrawalRequest buildFromUpdate(User user, Update update) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUser(user);
        withdrawalRequest.setStatus(WithdrawalRequestStatus.CREATED);
        withdrawalRequest.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
        withdrawalRequest.setActive(true);
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WithdrawalRequest that = (WithdrawalRequest) o;
        return Objects.equals(user, that.user) && Objects.equals(phoneNumber, that.phoneNumber) && status == that.status && Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, phoneNumber, status, isActive);
    }

    @Override
    public String toString() {
        return "WithdrawalRequest{" +
                "user=" + user +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status=" + status +
                ", isActive=" + isActive +
                '}';
    }
}
