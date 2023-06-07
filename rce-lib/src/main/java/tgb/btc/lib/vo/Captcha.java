package tgb.btc.lib.vo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@AllArgsConstructor
@NoArgsConstructor
public class Captcha {

    private String str;

    private InputFile image;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public InputFile getImage() {
        return image;
    }

    public void setImage(InputFile image) {
        this.image = image;
    }
}
