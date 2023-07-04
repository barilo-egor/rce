package tgb.btc.rce.bean;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "API_USER")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiUser extends BasePersist {

    @Getter
    @Setter
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
    private String token;

    @Getter
    @Setter
    @Column(length = 500)
    private String requisites;

    @Getter
    @Setter
    private BigDecimal usdCourse;
}
