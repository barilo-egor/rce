package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.vo.Captcha;

import java.io.File;
import java.util.*;

@Slf4j
@Service
public class CaptchaService {

    private static List<String> PICTURES_NAMES = new ArrayList<>();

    static {
        log.debug("Загрузка имен изображений-капч.");
        loadPicturesNames();
        log.debug("Загружено " + PICTURES_NAMES.size() + " капч.");
    }

    public static void loadPicturesNames() {
        File file = new File(FilePaths.CAPTCHA_PICTURES_PACKAGE);
        PICTURES_NAMES.addAll(Arrays.asList(Objects.requireNonNull(file.list())));
        //сделать
    }

    public Captcha getRandomCaptcha() {
        String pictureName = PICTURES_NAMES.get(new Random().ints(0, PICTURES_NAMES.size())
                .findFirst()
                .orElseThrow(() -> new BaseException("Ошибка при получении рандомного числа.")));

        File picture = new File(FilePaths.CAPTCHA_PICTURES_PACKAGE + "/" + pictureName);
        return new Captcha(pictureName.substring(0, pictureName.indexOf(".")), new InputFile(picture));
    }
}
