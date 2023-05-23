package tgb.btc.rce.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class InlineQueryCalculatorService {

    public String getQueryWithPoints(Update update) {
        return update.getInlineQuery().getQuery().replaceAll(",", ".");
    }

    public boolean hasEnteredAmount(String query) {
        return query.contains(" ");
    }
}
