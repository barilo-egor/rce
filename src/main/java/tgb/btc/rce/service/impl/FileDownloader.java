package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import tgb.btc.api.bot.IFileDownloader;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.sender.IResponseSender;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class FileDownloader implements IFileDownloader {

    private final RceBot bot;

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final String botToken;

    public FileDownloader(RceBot bot, IReadUserService readUserService, IResponseSender responseSender,
                          @Value("${bot.token}") String botToken) {
        this.bot = bot;
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.botToken = botToken;
    }

    @Override
    public void downloadFile(String fileId, String localPath) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            File file = bot.execute(getFile);
            java.io.File localFile = new java.io.File(localPath);
            URI uri = new URI(file.getFileUrl(botToken));
            InputStream is = uri.toURL().openStream();
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (Exception e) {
            String message = "Ошибка при скачивании файла из ТГ.";
            log.error(message, e);
            throw new BaseException(message, e);
        }
    }

    @Override
    public String saveFile(java.io.File file) {
        return saveFile(file, true);
    }

    @Override
    public String saveFile(java.io.File file, boolean delete) {
        List<Long> adminsChatIds = readUserService.getChatIdsByRoles(Set.of(UserRole.ADMIN, UserRole.OPERATOR));
        if (CollectionUtils.isEmpty(adminsChatIds)) {
            throw new BaseException("В БД отсутствуют администраторы, которым можно отправить чек для сохранения через ТГ.");
        }
        boolean sent = false;
        Message message = null;
        long sentChatId = 0;
        for (Long chatId : adminsChatIds) {
            message = sendFile(file, chatId);
            if (Objects.nonNull(message)) {
                sentChatId = chatId;
                sent = true;
                break;
            }
        }
        if (!sent) {
            throw new BaseException("Не получилось отправить файл для сохранения fileId в ТГ операторам или администраторам.");
        }
        responseSender.deleteMessage(sentChatId, message.getMessageId());
        if (delete && !FileUtils.deleteQuietly(file)) {
            log.warn("Не удалось удалить чек диспута из буфера: {} , name={}", file.getAbsolutePath(), file.getName());
        }
        return getFileId(message);
    }

    private String getFileId(Message message) {
        if (message.hasDocument()) {
            return message.getDocument().getFileId();
        } else if (message.hasAnimation()) {
            return message.getAnimation().getFileId();
        } else {
            List<PhotoSize> photoSizes = message.getPhoto();
            photoSizes.sort((p1, p2) -> p2.getHeight().compareTo(p1.getHeight()));
            return photoSizes.get(0).getFileId();
        }
    }

    private Message sendFile(java.io.File file, Long chatId) {
        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg")) {
            return responseSender.sendPhoto(chatId, null, new InputFile(file)).orElse(null);
        } else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".gif")) {
            return responseSender.sendAnimation(chatId, file);
        } else {
            return responseSender.sendFile(chatId, file);
        }
    }
}
