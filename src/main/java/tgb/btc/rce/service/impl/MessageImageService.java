package tgb.btc.rce.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tgb.btc.api.bot.IFileDownloader;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.service.IMessageImageService;
import tgb.btc.rce.vo.properties.FileId;
import tgb.btc.rce.vo.properties.FileIds;
import tgb.btc.rce.vo.properties.ImageMessages;
import tgb.btc.rce.vo.properties.MessageVariable;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class MessageImageService implements IMessageImageService {

    private static final String FILE_IDS_JSON_PATH = "config/design/message_images/fileIds.json";


    private static final String HELP_FILE_PATH = "config/design/message_images/help.txt";

    private static final String PNG_FORMAT = ".png";

    private static final String JPG_FORMAT = ".png";

    private static final List<String> FORMATS = List.of(".png", ".jpg", ".jpeg", ".mp4", ".gif");

    private static final String IMAGE_PATH = "config/design/message_images/%s%s";

    private static final String MESSAGES_JSON_PATH = "config/design/message_images/messages.json";

    private final IFileDownloader fileDownloader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<MessageImage, MessageVariable> MESSAGES = new HashMap<>();

    private final Map<MessageImage, FileId> FILES_IDS = new HashMap<>();

    private  FileIds fileIds;

    @Autowired
    public MessageImageService(IFileDownloader fileDownloader) {
        this.fileDownloader = fileDownloader;
    }

    @PostConstruct
    public void init() throws ConfigurationException, IOException {
        loadText();
        loadFileIds();
        loadHelp();
    }

    private void loadHelp() {
        File helpFile = new File(HELP_FILE_PATH);
        if (!helpFile.exists()) {
            log.debug("help.txt для изображений к сообщениям отсутствует и будет создан.");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HELP_FILE_PATH))) {
                writer.write("В папке config/design/message_images должны лежать изображения со следующими наименованиями:\n\n");
                for (MessageImage value : MessageImage.values()) {
                    writer.write(value.name() + PNG_FORMAT + " - " + value.getDescription());
                    writer.newLine();  // Переход на новую строку
                }
                writer.write("Доступные форматы на текущий момент: " + String.join(" ", FORMATS));
                writer.write("\nВ случае отсутствия изображения будет отправляться только текст.");
                writer.write("\n\nВ папке config/design/message_images также должен лежать " +
                        "messages.json(создается из заполняется автоматически при старте приложения)" +
                        " с аналогичными названиями проперти и текстами сообщений. Перенос строк обозначается как \\n\n" +
                        "После обновления уже существующей картинки следует удалить файл fileIds.json\n\n" +
                        "Данный файл help.txt создается автоматически при старте приложения. Его можно удалить и перезапустить" +
                        " бота, чтобы пересоздать файл и получить актуальные данные.");
                log.debug("help.txt успешно создан.");
            } catch (IOException e) {
                log.debug("Ошибка при создании help.txt", e);
            }
        }
    }

    private void loadFileIds() throws IOException {
        File fileFilesIds = new File(FILE_IDS_JSON_PATH);
        if (!fileFilesIds.exists()) {
            log.debug("Файл {} для хранения fileId изображений для сообщений не найден. Будет создан новый.", FILE_IDS_JSON_PATH);
            if (!fileFilesIds.createNewFile()) {
                log.error("Не получилось создать файл {}.", MESSAGES_JSON_PATH);
                throw new BaseException();
            }
            FileIds fileIds = new FileIds();
            fileIds.setFileIds(new ArrayList<>());
            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(fileFilesIds, fileIds);
        }

        fileIds = objectMapper.readValue(fileFilesIds, FileIds.class);

        for (MessageImage messageImage : MessageImage.values()) {
            Optional<FileId> optionalMessageVariable = fileIds.getFileIds()
                    .stream()
                    .filter(messageVariable -> messageVariable.getType().equals(messageImage))
                    .findFirst();
            if (optionalMessageVariable.isPresent()) {
                FileId messageVariable = optionalMessageVariable.get();
                if (StringUtils.isBlank(messageVariable.getFileId())) {
                    FILES_IDS.put(messageImage, messageVariable);
                    log.debug("FileId для {} найден и загружен в кеш.", messageImage.name());
                }
            }
        }
    }

    private void loadText() throws IOException {
        File messagesFile = new File(MESSAGES_JSON_PATH);
        if (!messagesFile.exists()) {
            log.debug("Отсутствует файл с текстами сообщений {} , будет создан новый.", MESSAGES_JSON_PATH);
            if (!messagesFile.createNewFile()) {
                log.error("Не получилось создать файл {}.", MESSAGES_JSON_PATH);
                throw new BaseException();
            }
            ImageMessages imageMessages = new ImageMessages();
            imageMessages.setMessages(new ArrayList<>());
            objectMapper.writeValue(messagesFile, imageMessages);
        }
        ImageMessages messages;
        try {
            messages = objectMapper.readValue(messagesFile, ImageMessages.class);
        } catch (Exception e) {
            log.error("Ошибка при попытке считать {}", MESSAGES_JSON_PATH);
            log.error(e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }
        for (MessageImage messageImage : MessageImage.values()) {
            Optional<MessageVariable> optionalMessageVariable = messages.getMessages()
                    .stream()
                    .filter(messageVariable -> messageVariable.getType().equals(messageImage))
                    .findFirst();
            if (optionalMessageVariable.isPresent()) {
                MessageVariable messageVariable = optionalMessageVariable.get();
                if (StringUtils.isBlank(messageVariable.getText())) {
                    MESSAGES.put(messageImage, MessageVariable.builder().type(messageImage).text(messageImage.getDefaultMessage()).build());
                    messageVariable.setText(messageImage.getDefaultMessage());
                    log.debug("У сообщения {} отсутствует текст. Будет установлен дефолтный.", messageImage.name());
                } else {
                    MESSAGES.put(messageImage, messageVariable);
                }
            } else {
                MESSAGES.put(messageImage, MessageVariable.builder().type(messageImage).text(messageImage.getDefaultMessage()).build());
                messages.getMessages().add(MessageVariable.builder()
                        .type(messageImage)
                        .text(messageImage.getDefaultMessage())
                        .build());
                log.debug("Отсутствует {} . Сообщение с дефолтным текстом будет добавлено.", messageImage.name());
            }
        }
        try {
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(messagesFile, messages);
        } catch (Exception e) {
            log.error("Ошибка при попытке записи {} значения: {}", MESSAGES_JSON_PATH, messages);
            log.error(e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }
    }

    @Override
    @Cacheable("messageImageFilesIdsCache")
    public String getFileId(MessageImage messageImage) {
        FileId fileId = FILES_IDS.get(messageImage);
        if (Objects.nonNull(fileId) && StringUtils.isNotBlank(fileId.getFileId())) {
            return fileId.getFileId();
        } else {
            File imageFile = null;
            String fileFormat = null;
            for (String format : FORMATS) {
                imageFile = new File(String.format(IMAGE_PATH, messageImage.name(), format));
                if (imageFile.exists()) {
                    fileFormat = format;
                    break;
                } else {
                    imageFile = null;
                }
            }
            if (Objects.isNull(imageFile)) {
                return null;
            }
            String strFileId = fileDownloader.saveFile(imageFile, false);
            fileId = FileId.builder().type(messageImage).format(fileFormat).fileId(strFileId).build();
            fileIds.getFileIds().add(fileId);
            FILES_IDS.put(messageImage, fileId);
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            try {
                objectWriter.writeValue(new File(FILE_IDS_JSON_PATH), fileIds);
            } catch (IOException e) {
                log.error("Ошибка при попытке записи fileId для {}.", messageImage.name());
                log.error(e.getMessage(), e);
                throw new BaseException(e.getMessage(), e);
            }
            log.debug("Было загружено новое изображение для {}.", messageImage.name());
            return strFileId;
        }
    }

    @Override
    @Cacheable("messageImageMessagesCache")
    public String getMessage(MessageImage messageImage) {
        MessageVariable value = MESSAGES.get(messageImage);
        return Objects.isNull(value) || StringUtils.isBlank(value.getText())
                ? messageImage.getDefaultMessage()
                : value.getText();
    }

    @Override
    @Cacheable("messageImageSubTypesCache")
    public Integer getSubType(MessageImage messageImage) {
        MessageVariable value = MESSAGES.get(messageImage);
        return Objects.isNull(value) || Objects.isNull(value.getSubType())
                ? 1
                : value.getSubType();
    }

    @Override
    @Cacheable("messageImageFormatCache")
    public String getFormat(MessageImage messageImage) {
        FileId fileId = FILES_IDS.get(messageImage);
        return Objects.nonNull(fileId)
                ? fileId.getFormat()
                : ".jpg";
    }
}
