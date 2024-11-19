package tgb.btc.rce.service.impl.util;

import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Objects;

@Service
public class CallbackDataService implements ICallbackDataService {

    public static final String SPLITTER = ":";

    @Override
    public CallbackQueryData fromData(String data) {
        if (Objects.isNull(data) || data.isBlank()) {
            return null;
        }
        String[] split = data.split(SPLITTER);
        if (split.length < 1) {
            return null;
        }
        try {
            return CallbackQueryData.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
