package tgb.btc.rce.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyButton {
    private String text;
    private boolean isRequestContact;
    private boolean isRequestLocation;
}