package tgb.btc.rce.service.impl.util;

import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CallbackDataService implements ICallbackDataService {

    public static final String SPLITTER = ":";

    @Override
    public String buildData(CallbackQueryData data, Object... arguments) {
        return data.name() + SPLITTER + Arrays.stream(arguments).map(Object::toString).collect(Collectors.joining(SPLITTER));
    }

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

    @Override
    public String getArgument(String data, int index) {
        String[] split = data.split(SPLITTER);
        if (split.length - 1 < index) {
            return null;
        }
        return split[index];
    }

    @Override
    public Long getLongArgument(String data, int index) {
        String argument = getArgument(data, index);
        if (argument == null) {
            return null;
        }
        try {
            return Long.parseLong(argument);
        } catch (NumberFormatException e) {
            throw new BaseException("Ошибка при парсинге к Long: data=" + data + ", index=" + index, e);
        }
    }

    @Override
    public Integer getIntArgument(String data, int index) {
        String argument = getArgument(data, index);
        if (argument == null) {
            return null;
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new BaseException("Ошибка при парсинге к Integer: data=" + data + ", index=" + index, e);
        }
    }

    @Override
    public Boolean getBoolArgument(String data, int index) {
        String argument = getArgument(data, index);
        if (argument == null) {
            return null;
        }
        if (Boolean.TRUE.toString().equalsIgnoreCase(argument)) {
            return true;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(argument)) {
            return false;
        }
        throw new BaseException("Ошибка при парсинге к Boolean: data=" + data + ", index=" + index);
    }
}
