package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOTTERY_WIN")
@AllArgsConstructor
@NoArgsConstructor
public class LotteryWin extends BasePersist{

    @ManyToOne
    private User user;

    @Column(name = "WON_DATE_TIME")
    private LocalDateTime wonDateTime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getWonDateTime() {
        return wonDateTime;
    }

    public void setWonDateTime(LocalDateTime localDateTime) {
        this.wonDateTime = localDateTime;
    }
}
