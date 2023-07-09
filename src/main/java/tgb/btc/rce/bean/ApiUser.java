package tgb.btc.rce.bean;

import lombok.*;

import javax.persistence.*;
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
}
