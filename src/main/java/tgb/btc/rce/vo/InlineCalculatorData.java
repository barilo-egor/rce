package tgb.btc.rce.vo;

import tgb.btc.rce.constants.BotStringConstants;

public class InlineCalculatorData {
    private final String buttonData;
    private final String number;

    public InlineCalculatorData(String callbackQueryData) {
        String[] data = callbackQueryData.split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        this.buttonData = data[1];
        this.number = data.length == 3 ? data[2] : null;
    }

    public String getButtonData() {
        return buttonData;
    }

    public String getNumber() {
        return number;
    }

}
