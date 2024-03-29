package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.library.util.properties.VariablePropertiesUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.BotImageUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.USER_ADDITIONAL_VERIFICATION)
public class UserAdditionalVerification extends Processor {

    private DealService dealService;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long dealPid = Long.parseLong(userService.getBufferVariable(chatId));
        if (!dealService.existByPid(dealPid)) {
            responseSender.sendMessage(chatId, "Заявки не существует.");
            userService.setDefaultValues(chatId);
            processToMainMenu(chatId);
            return;
        }
        if (update.getMessage().hasPhoto()) {
            String imageId = BotImageUtil.getImageId(update.getMessage().getPhoto());
            userService.getAdminsChatIds().forEach(adminChatId -> responseSender.sendPhoto(adminChatId,
                    "Верификация по заявке №" + dealPid, imageId));
            dealRepository.updateAdditionalVerificationImageIdByPid(dealPid, imageId);
            responseSender.sendMessage(UpdateUtil.getChatId(update),
                    "Спасибо, твоя верификация отправлена администратору.");
            userService.setDefaultValues(chatId);
            dealRepository.updateDealStatusByPid(DealStatus.VERIFICATION_RECEIVED, dealPid);
            processToMainMenu(chatId);
            return;
        } else if (update.getMessage().hasText() && update.getMessage().getText().equals("Отказаться от верификации")) {
            responseSender.sendMessage(chatId, "Ты отказался от верификации. " +
                    "Дальнейшая связь через оператора.", KeyboardUtil.buildInline(List.of(
                    InlineButton.builder()
                            .data(VariablePropertiesUtil.getVariable(VariableType.OPERATOR_LINK))
                            .text("Написать оператору")
                            .build()
            )));
            userService.getAdminsChatIds().forEach(adminChatId ->
                    responseSender.sendMessage(adminChatId, "Отказ от верификации по заявке №" + dealPid));
            userService.setDefaultValues(chatId);
            dealRepository.updateDealStatusByPid(DealStatus.VERIFICATION_REJECTED, dealPid);
            processToMainMenu(chatId);
            return;
        }
        responseSender.sendMessage(chatId, "Либо отправь фото, либо жми \"Отказаться от верификации\".");
    }
}
