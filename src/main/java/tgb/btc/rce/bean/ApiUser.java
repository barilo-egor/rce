package tgb.btc.rce.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import org.apache.commons.lang.BooleanUtils;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.interfaces.JsonConvertable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
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
    private String sellRequisite;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FiatCurrency fiatCurrency;

    @OneToMany
    @Setter
    private List<UsdApiUserCourse> usdApiUserCourseList;

    @OneToMany
    @Getter
    @Setter
    private List<ApiUserMinSum> apiUserMinSum;

    public String getRequisite(DealType dealType) {
        if (DealType.isBuy(dealType)) return buyRequisite.getRequisite();
        else return sellRequisite;
    }

    public List<UsdApiUserCourse> getUsdApiUserCourseList() {
        if (Objects.nonNull(usdApiUserCourseList)) {
            return usdApiUserCourseList;
        } else return new ArrayList<>();
    }

    @Override
    public ObjectNode toJson() {
        ObjectNode result = MainWebController.DEFAULT_MAPPER.createObjectNode()
                .put("pid", this.getPid())
                .put("id", this.getId())
                .put("personalDiscount", this.getPersonalDiscount())
                .put("registrationDate", this.getRegistrationDate().format(DateTimeFormatter.ISO_DATE))
                .put("isBanned", BooleanUtils.isTrue(this.getIsBanned()))
                .put("token", this.getToken())
                .put("buyRequisite", this.getBuyRequisite().getPid())
                .put("sellRequisite", this.getSellRequisite())
                .put("fiatCurrency", this.fiatCurrency.name());
        usdApiUserCourseList.stream()
                .filter(course -> FiatCurrency.BYN.equals(course.getFiatCurrency()))
                .findFirst()
                .ifPresent(course -> result.put("usdCourseBYN", course.getCourse()));
        usdApiUserCourseList.stream()
                .filter(course -> FiatCurrency.RUB.equals(course.getFiatCurrency()))
                .findFirst()
                .ifPresent(course -> result.put("usdCourseRUB", course.getCourse()));
        return result;
    }

    public UsdApiUserCourse getCourse(FiatCurrency fiatCurrency) {
        return usdApiUserCourseList.stream()
                .filter(course -> fiatCurrency.equals(course.getFiatCurrency()))
                .findFirst()
                .orElse(null);
    }
}
