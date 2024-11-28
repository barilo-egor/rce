package tgb.btc.rce.service.impl.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.captcha.IAntiSpam;
import tgb.btc.rce.service.captcha.ICaptchaService;
import tgb.btc.rce.vo.EmojiCaptcha;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmojiCaptchaService implements ICaptchaService {

    private static final Integer EMOJI_COUNT = 4;

    private ResponseSender responseSender;

    private IAntiSpam antiSpam;

    @Autowired
    public void setAntiSpam(IAntiSpam antiSpam) {
        this.antiSpam = antiSpam;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void send(Long chatId) {
        List<EmojiCaptcha> captchaList = new ArrayList<>(EMOJI_COUNT);
        List<Integer> numbers = new ArrayList<>(EMOJI_COUNT);

        for (int i = 0; i < EMOJI_COUNT; i++) {
            int randomInt = getRandomInt(CAPTCHA_LIST.size());
            while (numbers.contains(randomInt)) {
                randomInt = getRandomInt(CAPTCHA_LIST.size());
            }
            numbers.add(randomInt);
            captchaList.add(CAPTCHA_LIST.get(randomInt));
        }
        int rightAnswerNumber = getRandomInt(EMOJI_COUNT);
        EmojiCaptcha emojiCaptcha = captchaList.get(rightAnswerNumber);
        List<InlineButton> buttons = new ArrayList<>(EMOJI_COUNT);
        captchaList.forEach(emCap -> buttons.add(InlineButton.builder()
                .text(emCap.getEmoji())
                .data(emCap.getAnswer().equals(emojiCaptcha.getAnswer())
                        ? CallbackQueryData.RIGHT_CAPTCHA_ANSWER.name()
                        : CallbackQueryData.WRONG_CAPTCHA_ANSWER.name())
                .build()));

        responseSender.sendMessage(chatId, "Сработала антиспам система. Выберите " + emojiCaptcha.getAnswer() + ", чтобы продолжить.", buttons);
        antiSpam.putToCaptchaCash(chatId, emojiCaptcha.getAnswer());
    }

    private int getRandomInt(int rightBound) {
        return new Random().ints(0, rightBound).findFirst()
                .orElseThrow(() -> new BaseException("Ошибка при получении рандомного числа."));
    }

    private final List<EmojiCaptcha> CAPTCHA_LIST = List.of(
            EmojiCaptcha.builder().emoji("\uD83D\uDC8B").answer("губы").build(),
            EmojiCaptcha.builder().emoji("\uD83E\uDD16").answer("робота").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDE4A").answer("обезьяну").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDC7D").answer("инопланетянина").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDC40").answer("глаза").build(),
            EmojiCaptcha.builder().emoji("\uD83E\uDDE0").answer("мозг").build(),
            EmojiCaptcha.builder().emoji("\uD83E\uDEC1").answer("легкие").build(),
            EmojiCaptcha.builder().emoji("❤️").answer("сердце").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDC2C").answer("дельфина").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDC36").answer("щенка").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDC2D").answer("мышь").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF33").answer("дерево").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF1A").answer("луну").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF1E").answer("солнце").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF4F").answer("яблоко").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF69").answer("пончик").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF54").answer("бургер").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF5F").answer("картофель фри").build(),
            EmojiCaptcha.builder().emoji("⚽️").answer("футбольный мяч").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDFC0").answer("баскетбольный мяч").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDE97").answer("машину").build(),
            EmojiCaptcha.builder().emoji("⚖️").answer("весы").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDCA3").answer("бомбу").build(),
            EmojiCaptcha.builder().emoji("⚰️").answer("гроб").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDC89").answer("шприц").build(),
            EmojiCaptcha.builder().emoji("\uD83C\uDF88").answer("шарик").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDDBC").answer("картину").build(),
            EmojiCaptcha.builder().emoji("\uD83E\uDE86").answer("матрешку").build(),
            EmojiCaptcha.builder().emoji("\uD83E\uDDF7").answer("булавку").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDCCE").answer("скрепку").build(),
            EmojiCaptcha.builder().emoji("✂️").answer("ножницы").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDD0E").answer("лупу").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDFE5").answer("красный квадрат").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDFE6").answer("синий квадрат").build(),
            EmojiCaptcha.builder().emoji("\uD83D\uDFEA").answer("фиолетовый квадрат").build()
    );

}
