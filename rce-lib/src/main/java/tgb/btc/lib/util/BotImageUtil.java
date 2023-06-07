package tgb.btc.lib.util;

import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class BotImageUtil {
    private BotImageUtil() {
    }

    public static String getImageId(List<PhotoSize> image) {
        return Objects.requireNonNull(image.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null)).getFileId();
    }
}