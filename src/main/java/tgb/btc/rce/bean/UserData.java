package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import tgb.btc.rce.enums.DealType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USER_DATA")
@AllArgsConstructor
@NoArgsConstructor
public class UserData extends BasePersist {

    @OneToOne
    private User user;

    @Column(name = "LONG_VARIABLE")
    private Long longVariable;

    @Column(name = "STRING_VARIABLE")
    private String stringVariable;

    @Column(name = "DEAL_TYPE_VARIABLE")
    private DealType dealTypeVariable;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getLongVariable() {
        return longVariable;
    }

    public void setLongVariable(Long longVariable) {
        this.longVariable = longVariable;
    }

    public String getStringVariable() {
        return stringVariable;
    }

    public void setStringVariable(String stringVariable) {
        this.stringVariable = stringVariable;
    }

    public DealType getDealTypeVariable() {
        return dealTypeVariable;
    }

    public void setDealTypeVariable(DealType dealTypeVariable) {
        this.dealTypeVariable = dealTypeVariable;
    }

}