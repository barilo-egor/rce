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

    public static InlineButton buildData(String text, String data) {
        return build(InlineType.CALLBACK_DATA, text, data);
    }

    public static InlineButton build(InlineType inlineType, String text, String data) {
        return InlineButton.builder()
                .text(text)
                .data(data)
                .inlineType(inlineType)
                .build();
    }
}
