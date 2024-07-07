package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import tgb.btc.api.bot.IFileDownloader;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.bot.RceBot;

import java.io.InputStream;
import java.net.URL;

@Service
@Slf4j
public class FileDownloader implements IFileDownloader {

    private RceBot bot;

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
}
