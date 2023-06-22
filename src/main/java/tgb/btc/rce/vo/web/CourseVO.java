package tgb.btc.rce.vo.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class CourseVO {
   private List<Course> courses;

   @Data
   @NoArgsConstructor
   public static class Course {
      private FiatCurrency fiatCurrency;

      private DealType dealType;

      private CryptoCurrency cryptoCurrency;

      private BigDecimal value;
   }
}
