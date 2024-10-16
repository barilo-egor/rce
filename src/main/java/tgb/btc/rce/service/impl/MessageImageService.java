package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tgb.btc.api.bot.IFileDownloader;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.service.IMessageImageService;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MessageImageService implements IMessageImageService {

    private PropertiesConfiguration FILE_IDS_PROPERTIES;

    private final Map<MessageImage, String> FILES_IDS = new HashMap<>();

    private static final String FILE_IDS_PROPERTIES_PATH = "config/design/images/fileIds.properties";

    private static final String HELP_FILE_PATH = "config/design/images/help.txt";

    private static final String IMAGE_FORMAT = ".png";

    private static final String IMAGE_PATH = "config/design/images/%s" + IMAGE_FORMAT;

    private final IFileDownloader fileDownloader;

    @Autowired
    public MessageImageService(IFileDownloader fileDownloader) {
        this.fileDownloader = fileDownloader;
    }

    @PostConstruct
    public void init() throws ConfigurationException {
        File fileFilesIds = new File(FILE_IDS_PROPERTIES_PATH);
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.setFileName(FILE_IDS_PROPERTIES_PATH);
        config.setEncoding("UTF-8");
        config.setAutoSave(true);
        if (!fileFilesIds.exists()) {
            log.debug("Файл {} для хранения fileId изображений для сообщений не найден. Будет создан новый.", FILE_IDS_PROPERTIES_PATH);
            config.save();
        } else {
            log.debug("Файл {} для хранения fileId изображений для сообщений найден.", FILE_IDS_PROPERTIES_PATH);
            config.load();
            for (MessageImage messageImage : MessageImage.values()) {
                String name = messageImage.name();
                String fileId = config.getString(name, null);
                if (StringUtils.isNotBlank(fileId)) {
                    FILES_IDS.put(messageImage, fileId);
                    log.debug("File id для {} найден и загружен в кеш.", name);
                } else {
                    log.debug("File id для {} не найден.", name);
                }
            }
        }
        FILE_IDS_PROPERTIES = config;

        File helpFile = new File(HELP_FILE_PATH);
        if (!helpFile.exists()) {
            log.debug("help.txt для изображений к сообщениям отсутствует и будет создан.");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HELP_FILE_PATH))) {
                writer.write("В папке config/design/images должны лежать изображения со следующими наименованиями:\n");
                for (MessageImage value : MessageImage.values()) {
                    writer.write(value.name() + IMAGE_FORMAT + " - " + value.getDescription());
                    writer.newLine();  // Переход на новую строку
                }
                log.debug("help.txt успешно создан.");
            } catch (IOException e) {
                log.debug("Ошибка при создании help.txt", e);
            }
        }
    }

    @Override
    @Cacheable("messageImageFilesIdsCache")
    public String getFileId(MessageImage messageImage) {
        String fileId;
        if (FILES_IDS.containsKey(messageImage)) {
            fileId = FILES_IDS.get(messageImage);
        } else {
            File imageFile = new File(String.format(IMAGE_PATH, messageImage.name()));
            if (!imageFile.exists()) {
                return null;
            }
            fileId = fileDownloader.saveFile(imageFile, false);
            FILE_IDS_PROPERTIES.addProperty(messageImage.name(), fileId);
            try {
                FILE_IDS_PROPERTIES.save();
            } catch (ConfigurationException e) {
                log.error("Ошибка при сохранении {} ", FILE_IDS_PROPERTIES_PATH);
                log.error("Описание:", e);
                return null;
            }
        }
        return fileId;
    }
}
