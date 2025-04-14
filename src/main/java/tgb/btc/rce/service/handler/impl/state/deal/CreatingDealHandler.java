package tgb.btc.rce.service.handler.impl.state.deal;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.handler.IStateHandler;

@Service
public class CreatingDealHandler implements IStateHandler {

    @Override
    public void handle(Update update) {

    }

    @Override
    public UserState getUserState() {
        return UserState.CREATING_A_DEAL;
    }
}
