package tgb.btc.rce.enums;

import tgb.btc.library.exception.BaseException;

import java.util.Arrays;

public enum InlineCalculatorButton {

    NUMBER("number"),
    COMMA("."),
    DEL("del"),
    CURRENCY_SWITCHER("currency_switcher"),
    CANCEL("⬅\uFE0Fназад"),
    SWITCH_CALCULATOR("Переключить"),
    SWITCH_TO_MAIN_CALCULATOR("Переключить калькулятор"),
    READY("готово➡\uFE0F");


    final String data;

    InlineCalculatorButton(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public static InlineCalculatorButton getByData(String data) {
        return Arrays.stream(InlineCalculatorButton.values())
                .filter(t -> t.getData().equals(data))
                .findFirst()
                .orElseThrow(() -> new BaseException("Не найдена кнопка калькулятора: " + data));
    }
}
