package tgb.btc.rce.enums;

import lombok.Getter;
import tgb.btc.library.exception.BaseException;

import java.util.Arrays;

@Getter
public enum InlineCalculatorButton {

    NUMBER("number"),
    COMMA("."),
    DEL("del"),
    CURRENCY_SWITCHER("currency_switcher"),
    CANCEL("⬅️назад"),
    SWITCH_CALCULATOR("Переключить"),
    SWITCH_TO_MAIN_CALCULATOR("Переключить калькулятор"),
    READY("готово➡️");

    final String data;

    InlineCalculatorButton(String data) {
        this.data = data;
    }

    public static InlineCalculatorButton getByData(String data) {
        return Arrays.stream(InlineCalculatorButton.values())
                .filter(t -> t.getData().equals(data))
                .findFirst()
                .orElseThrow(() -> new BaseException("Не найдена кнопка калькулятора: " + data));
    }
}
