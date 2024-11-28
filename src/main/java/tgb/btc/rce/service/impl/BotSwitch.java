package tgb.btc.rce.service.impl;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import tgb.btc.library.service.properties.ConfigPropertiesReader;
import tgb.btc.rce.service.IBotSwitch;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BotSwitch implements IBotSwitch {

    private final AtomicBoolean isOn = new AtomicBoolean(true);

    private final ConfigPropertiesReader configPropertiesReader;

    public BotSwitch(ConfigPropertiesReader configPropertiesReader) {
        this.configPropertiesReader = configPropertiesReader;
    }

    @PostConstruct
    public void setIsOn() {
        Boolean turnOffOnStart = configPropertiesReader.getBoolean("turn.off.on.start");
        if (BooleanUtils.isTrue(turnOffOnStart)) setOn(false);
    }

    @Override
    public boolean isOn() {
        return isOn.get();
    }

    @Override
    public void setOn(boolean on) {
        isOn.set(on);
    }
}
