package tgb.btc.rce.web.vo;

public class WebResponse {
    private final boolean isSuccess;

    public WebResponse(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }
}
