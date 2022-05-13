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

    @Column(name = "SUM")
    private Integer sum;

    @Column(name = "STATUS")
    private WithdrawalRequestStatus status;

    public WithdrawalRequest() {
    }

    public WithdrawalRequest(User user, Integer sum, WithdrawalRequestStatus status) {
        this.user = user;
        this.sum = sum;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public WithdrawalRequestStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalRequestStatus status) {
        this.status = status;
    }
}
