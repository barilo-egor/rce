package tgb.btc.rce.bean;

import lombok.Builder;
import tgb.btc.rce.enums.WithdrawalRequestStatus;

import javax.persistence.*;

@Entity
@Table(name = "WITHDRAWAL_REQUEST")
@Builder
public class WithdrawalRequest extends BasePersist {
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "REQUESTED_SUM")
    private Integer requestedSum;

    @Column(name = "STATUS")
    private WithdrawalRequestStatus status;

    public WithdrawalRequest() {
    }

    public WithdrawalRequest(User user, Integer requestedSum, WithdrawalRequestStatus status) {
        this.user = user;
        this.requestedSum = requestedSum;
        this.status = status;
    }

    public static WithdrawalRequest buildFromUpdate(User user, Integer inputSum) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUser(user);
        withdrawalRequest.setStatus(WithdrawalRequestStatus.CREATED);
        withdrawalRequest.setRequestedSum(inputSum);
        return withdrawalRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRequestedSum() {
        return requestedSum;
    }

    public void setRequestedSum(Integer sum) {
        this.requestedSum = sum;
    }

    public WithdrawalRequestStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalRequestStatus status) {
        this.status = status;
    }
}
