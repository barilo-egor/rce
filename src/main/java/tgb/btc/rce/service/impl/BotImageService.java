package tgb.btc.rce.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.service.IBotImageService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BotImageService implements IBotImageService {

    @Override
    public String getImageId(List<PhotoSize> image) {
        if (CollectionUtils.isEmpty(image))
            throw new BaseException("Для поиска максимального размера фото передана пустая коллекция либо null.");
        Optional<PhotoSize> optionalPhotoSize = image.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize));
        if (optionalPhotoSize.isEmpty())
            throw new BaseException("Не найден ни один PhotoSize с максимальным fileSize.");
        return optionalPhotoSize.get().getFileId();
    }
}
