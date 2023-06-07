package tgb.btc.lib.vo.calculate;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class PromoCodeAmount {
    private String name;

    private BigDecimal discount;

    private BigDecimal amountWithDiscount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getAmountWithDiscount() {
        return amountWithDiscount;
    }

    public void setAmountWithDiscount(BigDecimal amountWithDiscount) {
        this.amountWithDiscount = amountWithDiscount;
    }
}
