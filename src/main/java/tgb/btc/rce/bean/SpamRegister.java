package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "REVIEW")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpamRegister extends BasePersist {
    @ManyToOne
    private User user;
}
