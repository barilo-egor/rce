package tgb.btc.rce.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.web.INotifier;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.KeyboardService;

@Service
public class Notifier implements INotifier {

    private AdminService adminService;

    private KeyboardService keyboardService;

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
}
