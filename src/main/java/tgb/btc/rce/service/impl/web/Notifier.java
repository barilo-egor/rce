package tgb.btc.rce.service.impl.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.util.properties.VariablePropertiesUtil;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.NotifyService;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class Notifier implements INotifier {

    private NotifyService notifyService;

    private KeyboardService keyboardService;

    private IResponseSender responseSender;

    private IDealUserService dealUserService;

    @Autowired
    public void setNotifyService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setDealUserService(IDealUserService dealUserService) {
        this.dealUserService = dealUserService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setAdminService(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Override
    public void notifyNewApiDeal(Long apiDealPid) {
        notifyService.notifyMessage("Поступила новая api сделка.", keyboardService.getShowApiDeal(apiDealPid),
                Set.of(UserRole.OPERATOR, UserRole.ADMIN));
    }

    @Override
    public void notifyDealAutoDeleted(Long chatId, Integer integer) {
        Integer dealActiveTime = VariablePropertiesUtil.getInt(VariableType.DEAL_ACTIVE_TIME);
        if (Objects.nonNull(integer)) responseSender.deleteMessage(chatId, integer);
        responseSender.sendMessage(chatId, String.format(MessagePropertiesUtil.getMessage("deal.deleted.auto"), dealActiveTime));
    }

    @Override
    public void notifyDealDeletedByAdmin(Long dealPid) {
        responseSender.sendMessage(dealUserService.getUserChatIdByDealPid(dealPid), MessagePropertiesUtil.getMessage("deal.deleted.by.admin"));
    }

    @Override
    public void sendNotify(Long chatId, String message) {
        responseSender.sendMessage(chatId, message);
    }

    @Override
    public void sendLoginRequest(Long chatId) {
        responseSender.sendMessage(chatId, "Кто-то пытается авторизоваться на сайте под вашим chat id. Если это не вы, то проигнорируйте это сообщение.",
                InlineButton.builder()
                        .inlineType(InlineType.CALLBACK_DATA)
                        .text(Command.SUBMIT_LOGIN.getText())
                        .data(Command.SUBMIT_LOGIN.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + chatId)
                        .build());
    }

    @Override
    public void sendChatIdConfirmRequest(Long chatId) {
        responseSender.sendMessage(chatId, "Кто-то пытается зарегистрироваться на сайте под вашим chat id. Если это не вы, то проигнорируйте это сообщение.",
                InlineButton.builder()
                        .inlineType(InlineType.CALLBACK_DATA)
                        .text(Command.SUBMIT_REGISTER.getText())
                        .data(Command.SUBMIT_REGISTER.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + chatId)
                        .build());
    }

    @Override
    public void sendFile(List<Long> list, File file) {
        list.forEach(chatId -> responseSender.sendFile(chatId, file));
    }

    @Override
    public void notifyAdmins(String s) {
        notifyService.notifyMessage(s, Set.of(UserRole.ADMIN));
    }
}
