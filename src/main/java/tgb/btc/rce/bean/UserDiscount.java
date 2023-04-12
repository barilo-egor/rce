package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "USER_DISCOUNT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDiscount extends BasePersist {

    @OneToOne
    private User user;

    @Column(name = "IS_RANK_DISCOUNT_ON")
    private Boolean isRankDiscountOn;

    @Column(name = "PERSONAL_BUY")
    private BigDecimal personalBuy;

    @Column(name = "PERSONAL_SELL")
    private BigDecimal personalSell;
}
