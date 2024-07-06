package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import tgb.btc.library.constants.strings.FilePaths;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.conditional.PictureCaptchaCondition;
import tgb.btc.rce.exception.PicturesNotFoundException;
import tgb.btc.rce.service.AntiSpam;
import tgb.btc.rce.service.ICaptchaService;
import tgb.btc.rce.vo.Captcha;

import java.io.File;
import java.util.*;

@Slf4j
@Service
@Conditional(PictureCaptchaCondition.class)
public class PictureCaptchaService implements ICaptchaService {

    private ResponseSender responseSender;

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    private static List<String> PICTURES_NAMES = new ArrayList<>();

    static {
        log.debug("Загрузка имен изображений-капч.");
        loadPicturesNames();
        log.debug("Загружено " + PICTURES_NAMES.size() + " капч.");
    }

    public static void loadPicturesNames() {
        File file = new File(FilePaths.CAPTCHA_PICTURES_PACKAGE);
        if (!file.exists()) {
            throw new PicturesNotFoundException("Папка для изображений капчи не найдена.");
        }
        if (file.list().length == 0) {
            throw new PicturesNotFoundException("Не найдено ни одного изображения капчи.");
        }
        PICTURES_NAMES.addAll(Arrays.asList(file.list()));
    }

    public Captcha getRandomCaptcha() {
        String pictureName = PICTURES_NAMES.get(new Random().ints(0, PICTURES_NAMES.size())
                .findFirst()
                .orElseThrow(() -> new BaseException("Ошибка при получении рандомного числа.")));

        File picture = new File(FilePaths.CAPTCHA_PICTURES_PACKAGE + "/" + pictureName);
        return new Captcha(pictureName.substring(0, pictureName.indexOf(".")), new InputFile(picture));
    }

    @Override
    public void send(Long chatId) {
        Captcha captcha = getRandomCaptcha();
        responseSender.sendPhoto(chatId, "Сработала антиспам система. Введите капчу, чтобы продолжить.",
                captcha.getImage());
        AntiSpam.CAPTCHA_CASH.put(chatId, captcha.getStr());
    }
}
