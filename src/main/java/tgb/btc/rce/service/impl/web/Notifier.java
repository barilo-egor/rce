package tgb.btc.rce.service.impl.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.bean.bot.GroupChat;
import tgb.btc.library.bean.web.api.ApiUser;
import tgb.btc.library.constants.enums.bot.GroupChatType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.library.interfaces.service.bean.web.IApiUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class Notifier implements INotifier {

    private INotifyService notifyService;

    private IKeyboardService keyboardService;

    private IResponseSender responseSender;

    private IDealUserService dealUserService;

    private DealSupportService dealSupportService;

    private IGroupChatService groupChatService;

    private IMessagePropertiesService messagePropertiesService;

    private VariablePropertiesReader variablePropertiesReader;

    private ICommandService commandService;

    private IApiUserService apiUserService;

    private IApiDealService apiDealService;

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Autowired
    public void setApiUserService(IApiUserService apiUserService) {
        this.apiUserService = apiUserService;
    }

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setGroupChatService(IGroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }

    @Autowired
    public void setDealSupportService(DealSupportService dealSupportService) {
        this.dealSupportService = dealSupportService;
    }

    @Autowired
    public void setNotifyService(INotifyService notifyService) {
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
    public void setKeyboardService(IKeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Override
    public void notifyNewApiDeal(Long apiDealPid) {
        notifyService.notifyMessage("Поступила новая api сделка.", keyboardService.getShowApiDeal(apiDealPid),
                Set.of(UserRole.OPERATOR, UserRole.ADMIN));
    }

    @Override
    public void notifyDealAutoDeleted(Long chatId, Integer integer) {
        Integer dealActiveTime = variablePropertiesReader.getInt(VariableType.DEAL_ACTIVE_TIME);
        if (Objects.nonNull(integer)) responseSender.deleteMessage(chatId, integer);
        responseSender.sendMessage(chatId, String.format(messagePropertiesService.getMessage("deal.deleted.auto"), dealActiveTime));
    }

    @Override
    public void notifyDealDeletedByAdmin(Long dealPid) {
        responseSender.sendMessage(dealUserService.getUserChatIdByDealPid(dealPid), messagePropertiesService.getMessage("deal.deleted.by.admin"));
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
                        .text(commandService.getText(Command.SUBMIT_LOGIN))
                        .data(Command.SUBMIT_LOGIN.name() + BotStringConstants.CALLBACK_DATA_SPLITTER + chatId)
                        .build());
    }

    @Override
    public void sendChatIdConfirmRequest(Long chatId) {
        responseSender.sendMessage(chatId, "Кто-то пытается зарегистрироваться на сайте под вашим chat id. Если это не вы, то проигнорируйте это сообщение.",
                InlineButton.builder()
                        .inlineType(InlineType.CALLBACK_DATA)
                        .text(commandService.getText(Command.SUBMIT_REGISTER))
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

    @Override
    public void sendRequestToWithdrawDeal(String from, String requestInitiator, Long dealPid) {
        Optional<GroupChat> optionalGroupChat = groupChatService.getByType(GroupChatType.DEAL_REQUEST);
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод сделки.");
        GroupChat groupChat = optionalGroupChat.get();
        String dealString = dealSupportService.dealToRequestString(dealPid);
        String result = "Запрос из <b>" + from + "</b> от <b>" + requestInitiator + "</b>.\n\n" + dealString;
        responseSender.sendMessage(groupChat.getChatId(), result, "html");
    }

    @Override
    public void sendRequestToWithdrawApiDeal(Long apiDealPid) {
        Optional<GroupChat> optionalGroupChat = groupChatService.getByApiUserPid(apiDealService.getApiUserPidByDealPid(apiDealPid));
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод апи сделки pid=" + apiDealPid);
        GroupChat groupChat = optionalGroupChat.get();
        String dealString = dealSupportService.apiDealToRequestString(apiDealPid);
        responseSender.sendMessage(groupChat.getChatId(), dealString, "html");
    }

    @Override
    public void sendGreetingToNewDealRequestGroup() {
        Optional<GroupChat> optionalGroupChat = groupChatService.getByType(GroupChatType.DEAL_REQUEST);
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод сделки.");
        GroupChat groupChat = optionalGroupChat.get();
        responseSender.sendMessage(groupChat.getChatId(), "Данная группа была выбрана для отправки запросов на вывод." +
                "Для того, чтобы узнать возможности бота, введите <code>/help</code>.", "html");
    }

    @Override
    public void sendGreetingToNewApiDealRequestGroup(Long apiUserPid) {
        ApiUser apiUser = apiUserService.findById(apiUserPid);
        Optional<GroupChat> optionalGroupChat = Optional.ofNullable(apiUser.getGroupChat());
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод апи сделки клиента pid=" + apiUserPid + ".");
        GroupChat groupChat = optionalGroupChat.get();
        responseSender.sendMessage(groupChat.getChatId(), "Данная группа была выбрана для отправки запросов на вывод API сделок клиента <b>" + apiUser.getId() +
                "</b>. Для того, чтобы узнать возможности бота, введите <code>/help</code>.", "html");
    }

    @Override
    public void sendGoodbyeToNewApiDealRequestGroup(Long chatId, String apiUserId) {
        responseSender.sendMessage(chatId, "Данная группа была отвязана от API клиента <b>" + apiUserId + "</b>.", "html");
    }
}
