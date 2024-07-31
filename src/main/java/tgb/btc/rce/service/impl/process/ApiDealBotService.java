package tgb.btc.rce.service.impl.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.bot.ReceiptFormat;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.process.IApiDealBotService;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

import static tgb.btc.rce.constants.BotStringConstants.CALLBACK_DATA_SPLITTER;

@Service
public class ApiDealBotService implements IApiDealBotService {

    private DealSupportService dealSupportService;

    private ICommandService commandService;

    private ICallbackQueryService callbackQueryService;

    private IApiDealService apiDealService;

    private IGroupChatService groupChatService;

    private IResponseSender responseSender;

    private IKeyboardBuildService keyboardBuildService;

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
                .text(commandService.getText(Command.CONFIRM_API_DEAL))
                .data(callbackQueryService.buildCallbackData(Command.CONFIRM_API_DEAL, new Object[]{pid, false}))
                .build());
        boolean hasDefaultGroupChat = groupChatService.hasGroupChat(apiDealService.getApiUserPidByDealPid(pid));
        if (hasDefaultGroupChat)
            buttons.add(InlineButton.builder()
                    .text(commandService.getText(Command.CONFIRM_API_DEAL) + " запросом")
                    .data(callbackQueryService.buildCallbackData(Command.CONFIRM_API_DEAL, new Object[]{pid, true}))
                    .build());
        buttons.add(InlineButton.builder()
                .text(commandService.getText(Command.CANCEL_API_DEAL))
                .data(Command.CANCEL_API_DEAL.name() + CALLBACK_DATA_SPLITTER + pid)
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
