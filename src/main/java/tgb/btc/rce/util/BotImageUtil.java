package tgb.btc.rce.util;

import org.apache.commons.collections4.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import tgb.btc.library.exception.BaseException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class BotImageUtil {
    private BotImageUtil() {
    }

    public static String getImageId(List<PhotoSize> image) {
        if (CollectionUtils.isEmpty(image))
            throw new BaseException("Для поиска максимального размера фото передана пустая коллекция либо null.");
        Optional<PhotoSize> optionalPhotoSize = image.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize));
        if (optionalPhotoSize.isEmpty())
            throw new BaseException("Не найден ни один PhotoSize с максимальным fileSize.");
        return optionalPhotoSize.get().getFileId();
    }
}