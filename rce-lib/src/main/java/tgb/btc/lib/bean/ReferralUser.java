package tgb.btc.lib.bean;

import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "REFERRAL_USER")
@Builder
public class ReferralUser extends BasePersist {
    @Column(name = "CHAT_ID")
    private Long chatId;

    @Column(name = "SUM")
    private Integer sum;

    public ReferralUser() {
    }

    public ReferralUser(Long chatId, Integer sum) {
        this.chatId = chatId;
        this.sum = sum;
    }

    public static ReferralUser buildDefault(Long chatId) {
        ReferralUser referralUser = new ReferralUser();
        referralUser.setChatId(chatId);
        referralUser.setSum(0);
        return referralUser;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReferralUser that = (ReferralUser) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(sum, that.sum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatId, sum);
    }

    @Override
    public String toString() {
        return "ReferralUser{" +
                "chatId=" + chatId +
                ", sum=" + sum +
                '}';
    }
}
