package tgb.btc.rce.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.util.properties.VariablePropertiesUtil;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.sender.IResponseSender;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.Objects;

@Service
public class Notifier implements INotifier {

    private AdminService adminService;

    private KeyboardService keyboardService;

    private IResponseSender responseSender;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
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
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void notifyNewApiDeal(Long apiDealPid) {
        adminService.notify("Поступила новая api сделка.", keyboardService.getShowApiDeal(apiDealPid));
    }

    @Override
    public void notifyDealAutoDeleted(Long chatId, Integer integer) {
        Integer dealActiveTime = VariablePropertiesUtil.getInt(VariableType.DEAL_ACTIVE_TIME);
        if (Objects.nonNull(integer)) responseSender.deleteMessage(chatId, integer);
        responseSender.sendMessage(chatId, String.format(MessagePropertiesUtil.getMessage("deal.deleted.auto"), dealActiveTime));
    }

    @Override
    public void notifyDealDeletedByAdmin(Long dealPid) {
        responseSender.sendMessage(dealRepository.getUserChatIdByDealPid(dealPid), MessagePropertiesUtil.getMessage("deal.deleted.by.admin"));
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
}
