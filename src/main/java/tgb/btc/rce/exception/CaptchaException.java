package tgb.btc.rce.exception;


import tgb.btc.library.exception.BaseException;

public class CaptchaException extends BaseException {
    public CaptchaException(){
    }

    public CaptchaException(String message) {
        super(message);
    }
}
