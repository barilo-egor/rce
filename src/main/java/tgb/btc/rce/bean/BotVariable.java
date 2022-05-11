package tgb.btc.rce.bean;

import lombok.Builder;
import tgb.btc.rce.enums.BotVariableType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "BOT_VARIABLE")
@Builder
public class BotVariable extends BasePersist {
    @Column(name = "TYPE")
    @Enumerated(value = EnumType.STRING)
    private BotVariableType type;

    @Column(name = "VALUE", length = 30)
    private String value;

    @Column(name = "CLAZZ", length = 30)
    private String clazz;

    public BotVariable() {
    }

    public BotVariable(BotVariableType type, String value, String clazz) {
        this.type = type;
        this.value = value;
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public BotVariableType getType() {
        return type;
    }

    public void setType(BotVariableType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BotVariable{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", clazz='" + clazz + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BotVariable that = (BotVariable) o;
        return type == that.type && Objects.equals(value, that.value) && Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, value, clazz);
    }
}
