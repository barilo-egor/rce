package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentConfig;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PaymentType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.PaymentConfigService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.PAYMENT_REQUISITES)
public class PaymentRequisites extends Processor {

    private final PaymentConfigService paymentConfigService;

    @Autowired
    public PaymentRequisites(IResponseSender responseSender, UserService userService,
                             PaymentConfigService paymentConfigService) {
        super(responseSender, userService);
        this.paymentConfigService = paymentConfigService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) {
            processToAdminMainPanel(chatId);
            return;
        }
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                askForInput(chatId);
                userService.nextStep(chatId, Command.PAYMENT_TYPES);
                break;
            case 1:
                String text = update.getMessage().getText();
                for (PaymentType paymentType : PaymentType.values()) {
                    if (text.startsWith(paymentType.getDisplayName())) {
                        PaymentConfig paymentConfig = paymentConfigService.getByPaymentType(paymentType);
                        paymentConfig.setRequisites(text);
                        paymentConfigService.save(paymentConfig);
                        responseSender.sendMessage(chatId, "Реквизиты " +
                                paymentType.getDisplayName() + " заменены.");
                        processToAdminMainPanel(chatId);
                        return;
                    }
                }
                break;
        }
    }

    private void askForInput(Long chatId) {
        List<ReplyButton> buttons = new ArrayList<>();
        for (PaymentType paymentType : PaymentType.values()) {
            buttons.add(ReplyButton.builder()
                    .text(paymentType.getDisplayName())
                    .build());
        }
        buttons.add(ReplyButton.builder()
                .text(Command.CANCEL.getText())
                .build());

        responseSender.sendMessage(chatId, "Выберите тип оплаты для замены реквизитов.",
                KeyboardUtil.buildReply(buttons));
    }
}
