package tgb.btc.rce.service.impl.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tgb.btc.api.web.INotifier;
import tgb.btc.library.bean.bot.GroupChat;
import tgb.btc.library.bean.web.api.ApiUser;
import tgb.btc.library.constants.enums.ApiDealType;
import tgb.btc.library.constants.enums.bot.GroupChatType;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.web.IApiDealService;
import tgb.btc.library.interfaces.service.bean.web.IApiUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.processors.support.DealSupportService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Profile("!web")
public class Notifier implements INotifier {

    private INotifyService notifyService;

    private IKeyboardService keyboardService;

    private IResponseSender responseSender;

    private IDealUserService dealUserService;

    private DealSupportService dealSupportService;

    private IGroupChatService groupChatService;

    private IMessagePropertiesService messagePropertiesService;

    private VariablePropertiesReader variablePropertiesReader;

    private IApiUserService apiUserService;

    private IApiDealService apiDealService;

    private ICallbackDataService callbackDataService;

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setApiDealService(IApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Autowired
    public void setApiUserService(IApiUserService apiUserService) {
        this.apiUserService = apiUserService;
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
        ApiDealType apiDealType = apiDealService.getApiDealTypeByPid(apiDealPid);
        String message = ApiDealType.API.equals(apiDealType)
                ? "Поступила новая api сделка."
                : "Поступил новый диспут.";
        notifyService.notifyMessage(message, keyboardService.getShowApiDeal(apiDealPid), UserRole.OBSERVER_ACCESS);
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
                        .text("Подтвердить вход")
                        .data(callbackDataService.buildData(CallbackQueryData.SUBMIT_LOGIN, chatId))
                        .build());
    }

    @Override
    public void sendChatIdConfirmRequest(Long chatId) {
        responseSender.sendMessage(chatId, "Кто-то пытается зарегистрироваться на сайте под вашим chat id. Если это не вы, то проигнорируйте это сообщение.",
                InlineButton.builder()
                        .inlineType(InlineType.CALLBACK_DATA)
                        .text("Подтвердить регистрацию")
                        .data(CallbackQueryData.SUBMIT_REGISTER.name())
                        .build());
    }

    @Override
    public void sendFile(List<Long> list, File file) {
        list.forEach(chatId -> responseSender.sendFile(chatId, file));
    }

    @Override
    public void notifyAdmins(String s) {
        notifyService.notifyMessage(s, UserRole.OPERATOR_ACCESS);
    }

    private void notifyAdmins(String s, Long excludeChatId) {
        notifyService.notifyMessage(s, Set.of(UserRole.ADMIN), List.of(excludeChatId));
    }

    @Override
    public void sendRequestToWithdrawDeal(String from, String requestInitiator, Long dealPid) {
        Optional<GroupChat> optionalGroupChat = groupChatService.getAllByType(GroupChatType.DEAL_REQUEST).stream().findAny();
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод сделки.");
        GroupChat groupChat = optionalGroupChat.get();
        String dealString = dealSupportService.dealToRequestString(dealPid);
        String result = "Запрос из <b>" + from + "</b> от <b>" + requestInitiator + "</b>.\n\n" + dealString;
        responseSender.sendMessage(groupChat.getChatId(), result);
    }

    @Override
    public void sendAutoWithdrawDeal(String from, String requestInitiator, Long dealPid) {
        Optional<GroupChat> optionalGroupChat = groupChatService.getAllByType(GroupChatType.AUTO_WITHDRAWAL).stream().findAny();
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки сделок с автовыводом.");
        GroupChat groupChat = optionalGroupChat.get();
        String dealString = dealSupportService.dealToRequestString(dealPid);
        String result = "Автовывод из <b>" + from + "</b> от <b>" + requestInitiator + "</b>.\n\n" + dealString;
        responseSender.sendMessage(groupChat.getChatId(), result);
    }

    @Override
    public void sendRequestToWithdrawApiDeal(Long apiDealPid) {
        Optional<GroupChat> optionalGroupChat = groupChatService.getByApiUserPid(apiDealService.getApiUserPidByDealPid(apiDealPid));
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод апи сделки pid=" + apiDealPid);
        GroupChat groupChat = optionalGroupChat.get();
        String dealString = dealSupportService.apiDealToRequestString(apiDealPid);
        responseSender.sendMessage(groupChat.getChatId(), dealString);
    }

    @Override
    public void sendGreetingToNewDealRequestGroup() {
        Optional<GroupChat> optionalGroupChat = groupChatService.getAllByType(GroupChatType.DEAL_REQUEST).stream().findAny();
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод сделки.");
        GroupChat groupChat = optionalGroupChat.get();
        responseSender.sendMessage(groupChat.getChatId(), "Данная группа была выбрана для отправки запросов на вывод." +
                "Для того, чтобы узнать возможности бота, введите <code>/help</code>.");
    }

    @Override
    public void sendGreetingToNewAutoWithdrawalGroup() {
        Optional<GroupChat> optionalGroupChat = groupChatService.getAllByType(GroupChatType.AUTO_WITHDRAWAL).stream().findAny();
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки автовыводов сделок.");
        GroupChat groupChat = optionalGroupChat.get();
        responseSender.sendMessage(groupChat.getChatId(), "Данная группа была выбрана для отправки автовыводов." +
                "Для того, чтобы узнать возможности бота, введите <code>/help</code>.");
    }

    @Override
    public void sendGreetingToNewApiDealRequestGroup(Long apiUserPid) {
        ApiUser apiUser = apiUserService.findById(apiUserPid);
        Optional<GroupChat> optionalGroupChat = Optional.ofNullable(apiUser.getGroupChat());
        if (optionalGroupChat.isEmpty())
            throw new BaseException("Не найдена дефолтная чат-группа для отправки запроса на вывод апи сделки клиента pid=" + apiUserPid + ".");
        GroupChat groupChat = optionalGroupChat.get();
        responseSender.sendMessage(groupChat.getChatId(), "Данная группа была выбрана для отправки запросов на вывод API сделок клиента <b>" + apiUser.getId() +
                "</b>. Для того, чтобы узнать возможности бота, введите <code>/help</code>.");
    }

    @Override
    public void sendGoodbyeToNewApiDealRequestGroup(Long chatId, String apiUserId) {
        responseSender.sendMessage(chatId, "Данная группа была отвязана от API клиента <b>" + apiUserId + "</b>.");
    }

    @Override
    public void notifyPoolChanged(Long aLong) {
        notifyAdmins("Пул сделок BTC был обновлен.", aLong);
    }

    @Override
    public void apiDealDeclined(Long aLong) {
        notifyAdmins("API сделка №" + aLong + " была отклонена по истечению времени.");
    }

    @Override
    public void merchantUpdateStatus(Long aLong, String s) {
        notifyService.notifyMessage(s, keyboardService.getShowDeal(aLong), UserRole.OPERATOR_ACCESS);
    }

    @Override
    public void notifyAutoConfirmDeal(String s, Long aLong) {
        notifyService.notifyMessage(s, keyboardService.getShowDeal(aLong), UserRole.OPERATOR_ACCESS);
    }
}
