package tgb.btc.rce.vo;

import lombok.*;
import tgb.btc.library.constants.enums.bot.FiatCurrency;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPrise {

    private int sum;

    private int minPrise;

    private int maxPrise;

    private FiatCurrency fiatCurrency;

}
