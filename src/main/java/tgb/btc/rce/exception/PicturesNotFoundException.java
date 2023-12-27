package tgb.btc.rce.exception;


public class PicturesNotFoundException extends CaptchaException {
    public PicturesNotFoundException(){
    }

    public PicturesNotFoundException(String message) {
        super(message);
    }
}
