package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "SPAM_BAN")
@AllArgsConstructor
@NoArgsConstructor
public class SpamBan extends BasePersist {
    @OneToOne
    private User user;

    @Column(name = "LOCAL_DATE_TIMEN")
    private LocalDateTime localDateTime;
}
