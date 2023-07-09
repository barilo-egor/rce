package tgb.btc.rce.bean;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import org.apache.commons.lang.BooleanUtils;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.interfaces.JsonConvertable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "API_USER")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiUser extends BasePersist implements JsonConvertable {

    @Getter
    @Setter
    @Column(unique = true)
    private String id;

    @Getter
    @Setter
    private BigDecimal personalDiscount;

    @Getter
    @Setter
    private LocalDate registrationDate;

    @Getter
    @Setter
    private Boolean isBanned;

    @Getter
    @Setter
    @Column(unique = true)
    private String token;

    @Getter
    @Setter
    @OneToOne
    private PaymentRequisite buyRequisite;

    @Getter
    @Setter
    private BigDecimal usdCourse;

    @Override
    public ObjectNode toJson() {
        return MainWebController.DEFAULT_MAPPER.createObjectNode()
                .put("pid", this.getPid())
                .put("id", this.getId())
                .put("personalDiscount", this.getPersonalDiscount())
                .put("registrationDate", this.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .put("isBanned", BooleanUtils.isTrue(this.getIsBanned()))
                .put("token", this.getToken())
                .put("buyRequisite", this.getBuyRequisite().getRequisite());
    }
}
