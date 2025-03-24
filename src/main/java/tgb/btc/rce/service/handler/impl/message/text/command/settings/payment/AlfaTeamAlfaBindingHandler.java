package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.impl.AlfaTeamBindingService;

import java.util.Objects;

@Service
public class AlfaTeamAlfaBindingHandler implements ITextCommandHandler {

    private final AlfaTeamBindingService alfaTeamBindingService;

    public AlfaTeamAlfaBindingHandler(AlfaTeamBindingService alfaTeamBindingService) {
        this.alfaTeamBindingService = alfaTeamBindingService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        alfaTeamBindingService.sendPaymentTypes(
                chatId,
                paymentType -> Objects.nonNull(paymentType.getAlfaTeamAlfaPaymentOption()),
                Merchant.ALFA_TEAM_ALFA);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ALFA_TEAM_ALFA_BINDING;
    }
}
