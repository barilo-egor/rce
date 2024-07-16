package tgb.btc.rce.service.impl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.ICallbackQueryService;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CallbackQueryService implements ICallbackQueryService {

    @Override
    public Integer messageId(Update update) {
        if (!update.hasCallbackQuery()) throw new BaseException("В update отсутствует CallbackQuery.");
        return update.getCallbackQuery().getMessage().getMessageId();
    }

    @Override
    public String getSplitData(Update update, int index) {
        return update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[index];
    }

    @Override
    public String getSplitData(CallbackQuery callbackQuery, int index) {
        return callbackQuery.getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[index];
    }

    @Override
    public Long getSplitLongData(Update update, int index) {
        return Long.parseLong(getSplitData(update, index));
    }

    @Override
    public Boolean getSplitBooleanData(Update update, int index) {
        return Boolean.parseBoolean(getSplitData(update, index));
    }

    @Override
    public <T> String buildCallbackData(Command command, T[] variables) {
        checkVariables(variables);
        String[] variablesToString = Arrays.stream(variables)
                .map(Object::toString).collect(Collectors.toList())
                .toArray(new String[]{});
        checkStringVariables(variablesToString);
        return command.getText().concat(BotStringConstants.CALLBACK_DATA_SPLITTER)
                .concat(String.join(BotStringConstants.CALLBACK_DATA_SPLITTER, variablesToString));
    }

    private void checkStringVariables(String... variables) {
        if (Arrays.stream(variables).anyMatch(StringUtils::isBlank))
            throw new BaseException("Одна из переменных пуста");
    }

    @SafeVarargs
    private <T> void checkVariables(T... variables) {
        if (Objects.isNull(variables) || variables.length == 0)
            throw new BaseException("Не передана ни один аргумент.");
        if (ArrayUtils.contains(variables, null))
            throw new BaseException("В переменных содержится null.");
    }

    @Override
    public boolean isBack(Update update) {
        return update.hasCallbackQuery() && Command.BACK.name().equals(update.getCallbackQuery().getData());
    }
}
