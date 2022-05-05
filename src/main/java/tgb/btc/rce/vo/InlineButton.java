package tgb.btc.rce.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InlineButton {
    private String text;
    private String data;
}
