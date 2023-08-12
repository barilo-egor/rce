package tgb.btc.rce.web.vo;

@SuppressWarnings("unused")
public class SuccessResponse<T> extends WebResponse {

    private final T body;

    public SuccessResponse() {
        this(null);
    }

    public SuccessResponse(T body) {
        super(true);
        this.body = body;
    }

    public T getBody() {
        return this.body;
    }

    public boolean hasBody() {
        return (this.body != null);
    }

}
