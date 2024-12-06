package tgb.btc.rce.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InlineCalculatorButtonTest {

    @Test
    protected void getByData() {
        for (InlineCalculatorButton inlineCalculatorButton : InlineCalculatorButton.values()) {
            assertEquals(inlineCalculatorButton, InlineCalculatorButton.getByData(inlineCalculatorButton.getData()));
        }
    }

}