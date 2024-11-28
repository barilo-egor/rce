package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.IBotImageService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Set;

@Service
public class AdditionalVerificationStateHandler implements IStateHandler {

    private final IStartService startService;

    private final IResponseSender responseSender;

    private final IReadDealService readDealService;

    private final IReadUserService readUserService;

    private final IKeyboardBuildService keyboardBuildService;

    private final VariablePropertiesReader variablePropertiesReader;

    private final INotifyService notifyService;

    private final IModifyDealService modifyDealService;

    private final INotificationsAPI notificationsAPI;

    private final IDealPropertyService dealPropertyService;

    private final IBotImageService botImageService;

    public AdditionalVerificationStateHandler(IStartService startService, IResponseSender responseSender,
                                              IReadDealService readDealService, IReadUserService readUserService,
                                              IKeyboardBuildService keyboardBuildService,
                                              VariablePropertiesReader variablePropertiesReader, INotifyService notifyService,
                                              IModifyDealService modifyDealService,
                                              INotificationsAPI notificationsAPI, IDealPropertyService dealPropertyService,
                                              IBotImageService botImageService) {
        this.startService = startService;
        this.responseSender = responseSender;
        this.readDealService = readDealService;
        this.readUserService = readUserService;
        this.keyboardBuildService = keyboardBuildService;
        this.variablePropertiesReader = variablePropertiesReader;
        this.notifyService = notifyService;
        this.modifyDealService = modifyDealService;
        this.notificationsAPI = notificationsAPI;
        this.dealPropertyService = dealPropertyService;
        this.botImageService = botImageService;
    }


    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !(update.getMessage().hasPhoto() || update.getMessage().hasText())) {
            responseSender.sendMessage(UpdateType.getChatId(update), "Либо отправь фото, либо жми \"Отказаться от верификации\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Long dealPid = Long.parseLong(readUserService.getBufferVariable(chatId));
        if (message.hasText() && message.getText().equals("Отказаться от верификации")) {
            responseSender.sendMessage(chatId, "Ты отказался от верификации. " +
                    "Дальнейшая связь через оператора.", keyboardBuildService.buildInline(List.of(
                    InlineButton.builder()
                            .data(variablePropertiesReader.getVariable(VariableType.OPERATOR_LINK))
                            .text("Написать оператору")
                            .build()
            )));
            notifyService.notifyMessage("Отказ от верификации по заявке №" + dealPid, Set.of(UserRole.OPERATOR, UserRole.ADMIN));
            modifyDealService.updateDealStatusByPid(DealStatus.VERIFICATION_REJECTED, dealPid);
            notificationsAPI.declinedVerificationReceived(dealPid);
            startService.processToMainMenu(chatId);
        }
        if (!readDealService.existsById(dealPid)) {
            responseSender.sendMessage(chatId, "Заявки не существует.");
            startService.processToMainMenu(chatId);
            return;
        }
        DealStatus dealStatus = dealPropertyService.getDealStatusByPid(dealPid);
        if (!DealStatus.AWAITING_VERIFICATION.equals(dealStatus)) {
            responseSender.sendMessage(chatId, "Заявка уже обработана.");
            startService.processToMainMenu(chatId);
            return;
        }
        String imageId = botImageService.getImageId(update.getMessage().getPhoto());
        modifyDealService.updateAdditionalVerificationImageIdByPid(dealPid, imageId);
        modifyDealService.updateDealStatusByPid(DealStatus.VERIFICATION_RECEIVED, dealPid);
        notificationsAPI.additionalVerificationReceived(dealPid);
        responseSender.sendMessage(chatId,
                "Спасибо, твоя верификация отправлена администратору.");
        notifyService.notifyMessageAndPhoto("Верификация по заявке №" + dealPid, imageId, Set.of(UserRole.OPERATOR, UserRole.ADMIN));
        startService.processToMainMenu(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.ADDITIONAL_VERIFICATION;
    }
}
