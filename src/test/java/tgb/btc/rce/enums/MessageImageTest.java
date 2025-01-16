package tgb.btc.rce.enums;

import org.junit.jupiter.api.Test;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.enums.MessageImage;

import static org.junit.jupiter.api.Assertions.*;

class MessageImageTest {

    @Test
    void getInputWallet() {
        for (CryptoCurrency cryptoCurrency : CryptoCurrency.values()) {
            switch (cryptoCurrency) {
                case BITCOIN -> assertEquals(MessageImage.BITCOIN_INPUT_WALLET, MessageImage.getInputWallet(cryptoCurrency));
                case LITECOIN -> assertEquals(MessageImage.LITECOIN_INPUT_WALLET, MessageImage.getInputWallet(cryptoCurrency));
                case USDT -> assertEquals(MessageImage.USDT_INPUT_WALLET, MessageImage.getInputWallet(cryptoCurrency));
                case MONERO -> assertEquals(MessageImage.MONERO_INPUT_WALLET, MessageImage.getInputWallet(cryptoCurrency));
            }
        }
    }
}