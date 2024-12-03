package tgb.btc.rce.exception;

import tgb.btc.library.exception.BaseException;

public class HandlerTypeNotFoundException extends BaseException {
    public HandlerTypeNotFoundException(String message) {
        super(message);
    }
}
