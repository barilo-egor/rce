package tgb.btc.rce.service.impl.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.process.IApiDealBotService;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiDealBotService implements IApiDealBotService {

    private DealSupportService dealSupportService;

    private ICommandService commandService;

    private ICallbackQueryService callbackQueryService;

    private IApiDealService apiDealService;

    private IGroupChatService groupChatService;

    private IResponseSender responseSender;

    private IKeyboardBuildService keyboardBuildService;

    private ICallbackDataService callbackDataService;

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setDealSupportService(DealSupportService dealSupportService) {
        this.dealSupportService = dealSupportService;
    }

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public void setCallbackQueryService(ICallbackQueryService callbackQueryService) {
        this.callbackQueryService = callbackQueryService;
    }

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Autowired
    public void setGroupChatService(IGroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void sendApiDeal(Long pid, Long chatId) {
        ApiDeal apiDeal = apiDealService.getByPid(pid);
        String dealInfo = dealSupportService.apiDealToString(apiDeal);
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder()
                .text("Подтвердить")
                .data(callbackDataService.buildData(CallbackQueryData.CONFIRM_API_DEAL, pid, false))
                .build());
        boolean hasDefaultGroupChat = groupChatService.hasGroupChat(apiDealService.getApiUserPidByDealPid(pid));
        if (hasDefaultGroupChat)
            buttons.add(InlineButton.builder()
                    .text("Подтвердить с запросом")
                    .data(callbackDataService.buildData(CallbackQueryData.CONFIRM_API_DEAL, pid, true))
                    .build());
        buttons.add(InlineButton.builder()
                .text("Отклонить")
                .data(callbackDataService.buildData(CallbackQueryData.CANCEL_API_DEAL, pid))
                .build());
        responseSender.sendMessage(chatId, dealInfo, keyboardBuildService.buildInline(buttons));
        ApiDealType apiDealType = apiDealService.getApiDealTypeByPid(pid);
        if (ApiDealType.DISPUTE.equals(apiDealType)) {
            String caption = "Диспут №" + pid;
            if (ReceiptFormat.PDF.equals(apiDeal.getReceiptFormat())) {
                responseSender.sendFile(chatId, caption, apiDeal.getCheckImageId());
            } else {
                responseSender.sendPhoto(chatId, caption, apiDealService.getCheckImageIdByPid(pid));
            }
        }
    }
}
