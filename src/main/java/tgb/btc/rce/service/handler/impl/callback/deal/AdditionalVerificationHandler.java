package tgb.btc.rce.service.handler.impl.callback.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.api.bot.AdditionalVerificationProcessor;
import tgb.btc.library.constants.enums.bot.DealStatus;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@Service
@Slf4j
public class AdditionalVerificationHandler implements ICallbackQueryHandler, AdditionalVerificationProcessor {

    private final IDealUserService dealUserService;

    private final IModifyDealService modifyDealService;

    private final IResponseSender responseSender;

    private final IModifyUserService modifyUserService;

    private final IKeyboardBuildService keyboardBuildService;

    private final ICallbackDataService callbackDataService;

    public AdditionalVerificationHandler(IDealUserService dealUserService, IModifyDealService modifyDealService,
                                         IResponseSender responseSender, IModifyUserService modifyUserService,
                                         IKeyboardBuildService keyboardBuildService, ICallbackDataService callbackDataService) {
        this.dealUserService = dealUserService;
        this.modifyDealService = modifyDealService;
        this.responseSender = responseSender;
        this.modifyUserService = modifyUserService;
        this.keyboardBuildService = keyboardBuildService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long dealPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        ask(dealPid);
        Long chatId = callbackQuery.getFrom().getId();
        log.debug("Админ chatId={} запросил верификацию по сделке {}.", chatId, dealPid);
        responseSender.sendMessage(chatId, "Дополнительная верификация запрошена.");
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

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ADDITIONAL_VERIFICATION;
    }
}
