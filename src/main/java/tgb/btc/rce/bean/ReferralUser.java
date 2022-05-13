package tgb.btc.rce.bean;

import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
@Builder
public class ReferralUser extends BasePersist{
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
}
