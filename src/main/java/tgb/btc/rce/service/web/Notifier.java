package tgb.btc.rce.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.util.properties.VariablePropertiesUtil;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.util.MessagePropertiesUtil;

import java.util.Objects;

@Service
public class Notifier implements INotifier {

    private AdminService adminService;

    private KeyboardService keyboardService;

    private IResponseSender responseSender;

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
    public void sendNotify(Long chatId, String message) {
        responseSender.sendMessage(chatId, message);
    }
}
