package tgb.btc.rce.vo;

import lombok.*;
import tgb.btc.rce.enums.DealStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DealVO {
    private Long pid;

    private DealStatus dealStatus;

    private Long chatId;
}
