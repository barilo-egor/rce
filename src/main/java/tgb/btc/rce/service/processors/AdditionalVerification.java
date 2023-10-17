package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.library.service.bean.bot.DealService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.ADDITIONAL_VERIFICATION)
public class AdditionalVerification extends Processor {

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
        Long dealPid = Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        Long userChatId = dealService.getUserChatIdByDealPid(dealPid);
        dealRepository.updateDealStatusByPid(DealStatus.AWAITING_VERIFICATION, dealPid);
        userRepository.nextStep(userChatId, Command.USER_ADDITIONAL_VERIFICATION.name());
        userService.updateBufferVariable(userChatId, dealPid.toString());
        responseSender.sendMessage(userChatId,
                "⚠️Уважаемый клиент, необходимо пройти дополнительную верификацию. Предоставьте фото карты " +
                        "с которой была оплата на фоне переписки с ботом, либо бумажного чека на фоне переписки с " +
                        "ботом для завершения сделки. (Проверка проходится только при первом обмене)",
                KeyboardUtil.buildReply(List.of(ReplyButton.builder().text("Отказаться от верификации").build())));
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Дополнительная верификация запрошена.");
    }
}
