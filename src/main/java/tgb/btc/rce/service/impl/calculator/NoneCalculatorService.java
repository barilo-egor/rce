package tgb.btc.rce.service.impl.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import java.util.List;

public class NoneCalculatorService extends SimpleCalculatorService {

    private IRedisUserStateService redisUserStateService;

    @Autowired
    public void setRedisUserStateService(IRedisUserStateService redisUserStateService) {
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public ReplyKeyboard getKeyboard(Long chatId) {
        return keyboardBuildService.buildInline(List.of(keyboardBuildService.getInlineBackButton()), 1);
    }

    @Override
    public void setState(Long chatId) {
        redisUserStateService.save(chatId, UserState.NONE_CALCULATOR);
    }

}
