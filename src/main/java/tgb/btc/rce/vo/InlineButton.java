package tgb.btc.rce.vo;

import lombok.Builder;
import lombok.Data;
import tgb.btc.rce.enums.InlineType;

@Data
@Builder
public class InlineButton {
    private String text;
    private String data;
    private InlineType inlineType;
}
