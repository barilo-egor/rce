package tgb.btc.rce.service.processors.admin.requests.deal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.api.bot.AdditionalVerificationProcessor;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@CommandProcessor(command = Command.ADDITIONAL_VERIFICATION)
public class AdditionalVerification extends Processor implements AdditionalVerificationProcessor {

    private IDealUserService dealUserService;

    private IModifyDealService modifyDealService;

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Autowired
    public void setDealUserService(IDealUserService dealUserService) {
        this.dealUserService = dealUserService;
    }

    @Override
    public void run(Update update) {
        Long dealPid = Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]);
        ask(dealPid);
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Дополнительная верификация запрошена.");
    }

    public void ask(Long dealPid) {
        Long userChatId = dealUserService.getUserChatIdByDealPid(dealPid);
        modifyDealService.updateDealStatusByPid(DealStatus.AWAITING_VERIFICATION, dealPid);
        modifyUserService.nextStep(userChatId, Command.USER_ADDITIONAL_VERIFICATION.name());
        modifyUserService.updateBufferVariable(userChatId, dealPid.toString());
        responseSender.sendMessage(userChatId,
                "⚠️Уважаемый клиент, необходимо пройти дополнительную верификацию. Предоставьте фото карты " +
                        "с которой была оплата на фоне переписки с ботом, либо бумажного чека на фоне переписки с " +
                        "ботом для завершения сделки. (Проверка проходится только при первом обмене)",
                keyboardBuildService.buildReply(List.of(ReplyButton.builder().text("Отказаться от верификации").build())));
    }
}
