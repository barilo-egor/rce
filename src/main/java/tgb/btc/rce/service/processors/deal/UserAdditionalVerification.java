package tgb.btc.rce.service.processors.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.service.bean.bot.deal.read.DealPropertyService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.IBotImageService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Set;

@CommandProcessor(command = Command.USER_ADDITIONAL_VERIFICATION)
public class UserAdditionalVerification extends Processor {

    private IReadDealService readDealService;

    private IModifyDealService modifyDealService;

    private INotificationsAPI notificationsAPI;

    private INotifyService notifyService;

    private IBotImageService botImageService;

    private VariablePropertiesReader variablePropertiesReader;

    private DealPropertyService dealPropertyService;

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setBotImageService(IBotImageService botImageService) {
        this.botImageService = botImageService;
    }

    @Autowired
    public void setNotifyService(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setNotificationsAPI(INotificationsAPI notificationsAPI) {
        this.notificationsAPI = notificationsAPI;
    }

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Autowired
    public void setDealPropertyService(DealPropertyService dealPropertyService) {
        this.dealPropertyService = dealPropertyService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long dealPid = Long.parseLong(readUserService.getBufferVariable(chatId));
        if (!readDealService.existsById(dealPid)) {
            responseSender.sendMessage(chatId, "Заявки не существует.");
            modifyUserService.setDefaultValues(chatId);
            processToMainMenu(chatId);
            return;
        }
        DealStatus dealStatus = dealPropertyService.getDealStatusByPid(dealPid);
        if (!DealStatus.AWAITING_VERIFICATION.equals(dealStatus)) {
            responseSender.sendMessage(chatId, "Заявка уже обработана.");
            modifyUserService.setDefaultValues(chatId);
            processToMainMenu(chatId);
            return;
        }
        if (update.getMessage().hasPhoto()) {
            String imageId = botImageService.getImageId(update.getMessage().getPhoto());
            modifyDealService.updateAdditionalVerificationImageIdByPid(dealPid, imageId);
            modifyUserService.setDefaultValues(chatId);
            modifyDealService.updateDealStatusByPid(DealStatus.VERIFICATION_RECEIVED, dealPid);
            notificationsAPI.additionalVerificationReceived(dealPid);
            responseSender.sendMessage(updateService.getChatId(update),
                    "Спасибо, твоя верификация отправлена администратору.");
            notifyService.notifyMessageAndPhoto("Верификация по заявке №" + dealPid, imageId, Set.of(UserRole.OPERATOR, UserRole.ADMIN));
            processToMainMenu(chatId);
            return;
        } else if (update.getMessage().hasText() && update.getMessage().getText().equals("Отказаться от верификации")) {
            responseSender.sendMessage(chatId, "Ты отказался от верификации. " +
                    "Дальнейшая связь через оператора.", keyboardBuildService.buildInline(List.of(
                    InlineButton.builder()
                            .data(variablePropertiesReader.getVariable(VariableType.OPERATOR_LINK))
                            .text("Написать оператору")
                            .build()
            )));
            notifyService.notifyMessage("Отказ от верификации по заявке №" + dealPid, Set.of(UserRole.OPERATOR, UserRole.ADMIN));
            modifyUserService.setDefaultValues(chatId);
            modifyDealService.updateDealStatusByPid(DealStatus.VERIFICATION_REJECTED, dealPid);
            notificationsAPI.declinedVerificationReceived(dealPid);
            processToMainMenu(chatId);
            return;
        }
        responseSender.sendMessage(chatId, "Либо отправь фото, либо жми \"Отказаться от верификации\".");
    }
}
