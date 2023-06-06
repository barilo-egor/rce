package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.BotVariableType;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.DealService;
import tgb.btc.lib.util.BotImageUtil;
import tgb.btc.lib.util.BotVariablePropertiesUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.USER_ADDITIONAL_VERIFICATION)
public class UserAdditionalVerification extends Processor {

    private DealService dealService;

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
        if(update.getMessage().hasPhoto()) {
            userService.getAdminsChatIds().forEach(adminChatId -> responseSender.sendPhoto(adminChatId,
                    "Верификация по заявке №" + dealPid,
                    BotImageUtil.getImageId(update.getMessage().getPhoto())));
            responseSender.sendMessage(UpdateUtil.getChatId(update),
                    "Спасибо, твоя верификация отправлена администратору.");
            userService.setDefaultValues(chatId);
            processToMainMenu(chatId);
            return;
        }
        if(update.getMessage().hasText() && update.getMessage().getText().equals("Отказаться от верификации")) {
            responseSender.sendMessage(chatId, "Ты отказался от верификации. " +
                    "Дальнейшая связь через оператора.", KeyboardUtil.buildInline(List.of(
                    InlineButton.builder()
                            .data(BotVariablePropertiesUtil.getVariable(BotVariableType.OPERATOR_LINK))
                            .text("Написать оператору")
                            .build()
            )));
            userService.getAdminsChatIds().forEach(adminChatId ->
                    responseSender.sendMessage(adminChatId, "Отказ от верификации по заявке №" + dealPid));
            userService.setDefaultValues(chatId);
            processToMainMenu(chatId);
            return;
        }
        responseSender.sendMessage(chatId, "Либо отправь фото, либо жми \"Отказаться от верификации\".");
    }
}
