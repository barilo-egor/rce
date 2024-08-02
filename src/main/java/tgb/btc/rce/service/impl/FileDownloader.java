package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class FileDownloader implements IFileDownloader {

    private RceBot bot;

    private IReadUserService readUserService;

    private IResponseSender responseSender;

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setBot(RceBot bot) {
        this.bot = bot;
    }

    @Override
    public void downloadFile(String fileId, String localPath) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            File file = bot.execute(getFile);
            java.io.File localFile = new java.io.File(localPath);
            InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (Exception e) {
            String message = "Ошибка при скачивании файла из ТГ.";
            log.error(message, e);
            throw new BaseException(message, e);
        }
    }

    @Override
    public String saveFile(java.io.File file) {
        List<Long> adminsChatIds = readUserService.getChatIdsByRoles(Set.of(UserRole.ADMIN, UserRole.OPERATOR));
        if (CollectionUtils.isEmpty(adminsChatIds)) {
            throw new BaseException("В БД отсутствуют администраторы, которым можно отправить чек для сохранения через ТГ.");
        }
        boolean sent = false;
        Message message = null;
        long sentChatId = 0;
        for (Long chatId : adminsChatIds) {
            try {
                if (file.getName().endsWith(".pdf")) {
                    message = responseSender.sendFile(chatId, file);
                    sentChatId = chatId;
                } else {
                    message = responseSender.sendPhoto(chatId, null,
                            new InputFile(file)).orElse(null);
                    sentChatId = chatId;
                }
                sent = true;
                break;
            } catch (Exception ignored) {
            }
        }
        if (!sent || Objects.isNull(message) || sentChatId == 0) {
            throw new BaseException("Не получилось отправить чек диспута в ТГ операторам или администраторам.");
        }
        String result;
        if (file.getName().endsWith(".pdf")) {
            result = message.getDocument().getFileId();
        } else {
            List<PhotoSize> photoSizes = message.getPhoto();
            photoSizes.sort((p1, p2) -> p2.getHeight().compareTo(p1.getHeight()));
            result = photoSizes.get(0).getFileId();
        }
        responseSender.deleteMessage(sentChatId, message.getMessageId());
        if (!FileUtils.deleteQuietly(file)) {
            log.warn("Не удалось удалить чек диспута из буфера: {} , name={}", file.getAbsolutePath(), file.getName());
        }
        return result;
    }
}
