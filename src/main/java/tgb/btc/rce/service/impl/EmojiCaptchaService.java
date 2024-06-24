package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.conditional.EmojiCaptchaCondition;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.ICaptchaService;
import tgb.btc.rce.service.sender.ResponseSender;
import tgb.btc.rce.vo.EmojiCaptcha;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Random;

@Conditional(EmojiCaptchaCondition.class)
@Service
public class EmojiCaptchaService implements ICaptchaService {

    private final List<EmojiCaptcha> CAPTCHA_LIST = List.of(
            EmojiCaptcha.builder().emoji("\uD83D\uDC8B").answer("сердце").build()
    );

    private ResponseSender responseSender;

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void send(Long chatId) {
        EmojiCaptcha emojiCaptcha = CAPTCHA_LIST.get(new Random().ints(0, CAPTCHA_LIST.size()).findFirst()
                .orElseThrow(() -> new BaseException("Ошибка при получении рандомного числа.")));
        responseSender.sendMessage(chatId, "Сработала антиспам система. Выберите " + emojiCaptcha.getAnswer() + ", чтобы продолжить.",
                List.of(InlineButton.builder()
                        .text(emojiCaptcha.getEmoji())
                        .data(emojiCaptcha.getAnswer())
                        .build()));
        AntiSpam.CAPTCHA_CASH.put(chatId, emojiCaptcha.getAnswer());
    }
}
