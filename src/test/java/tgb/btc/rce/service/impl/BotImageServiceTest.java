package tgb.btc.rce.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.service.impl.util.BotImageService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BotImageServiceTest {

    private final BotImageService botImageService = new BotImageService();

    @Test
    void getImageId() {
        int numberOfTries = 3;
        for (int i = 1; i <= numberOfTries; i++) {
            List<PhotoSize> photoSizeList = new ArrayList<>();
            PhotoSize maxPhotoSize = new PhotoSize();
            int randomMaxSize = RandomUtils.secure().randomInt(1000, 9999);
            maxPhotoSize.setFileSize(randomMaxSize);
            String randomFileId = RandomStringUtils.secure().nextAlphanumeric(10);
            maxPhotoSize.setFileId(randomFileId);
            photoSizeList.add(maxPhotoSize);
            PhotoSize photoSize = new PhotoSize();
            photoSize.setFileSize(RandomUtils.secure().randomInt(1, 999));
            photoSizeList.add(photoSize);
            assertEquals(randomFileId, botImageService.getImageId(photoSizeList));
        }
    }

    @Test
    void getImageIdThrows() {
        assertThrows(BaseException.class, () -> botImageService.getImageId(new ArrayList<>()));
        assertThrows(BaseException.class, () -> botImageService.getImageId(null));
    }

}