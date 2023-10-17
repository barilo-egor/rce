package tgb.btc.rce.vo.web;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;

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
