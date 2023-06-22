package tgb.btc.rce.vo.web;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CourseVO {
   @Getter
   @Setter
   private FiatCurrency fiatCurrency;

   @Getter
   @Setter
   private DealType dealType;

   @Getter
   @Setter
   private CryptoCurrency cryptoCurrency;

   @Getter
   @Setter
   private BigDecimal value;
}
