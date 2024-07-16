package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;

public interface IBotImageService {


    String getImageId(List<PhotoSize> image);
}
