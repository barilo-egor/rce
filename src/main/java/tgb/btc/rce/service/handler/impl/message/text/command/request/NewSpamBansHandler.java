package tgb.btc.rce.service.handler.impl.message.text.command.request;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IUserInfoService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

import java.util.List;

@Service
public class NewSpamBansHandler implements ITextCommandHandler {

    private final ISpamBanService spamBanService;

    private final IResponseSender responseSender;

    private final IUserInfoService userInfoService;

    public NewSpamBansHandler(ISpamBanService spamBanService, IResponseSender responseSender,
                              IUserInfoService userInfoService) {
        this.spamBanService = spamBanService;
        this.responseSender = responseSender;
        this.userInfoService = userInfoService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        List<Long> pids = spamBanService.getPids();
        if (CollectionUtils.isEmpty(pids)) {
            responseSender.sendMessage(chatId, "Список пуст.");
            return;
        }
        for (Long spamBanPid : pids) {
            userInfoService.sendSpamBannedUser(chatId, spamBanPid);
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.NEW_SPAM_BANS;
    }
}
