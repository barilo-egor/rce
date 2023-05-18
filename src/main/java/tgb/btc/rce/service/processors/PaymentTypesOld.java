package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentConfig;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PaymentTypeEnum;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.PaymentConfigService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.PAYMENT_TYPES_OLD)
public class PaymentTypesOld extends Processor {

    private final PaymentConfigService paymentConfigService;

    @Autowired
    public PaymentTypesOld(IResponseSender responseSender, UserService userService, PaymentConfigService paymentConfigService) {
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
                userService.nextStep(chatId, Command.PAYMENT_TYPES_OLD);
                break;
            case 1:
                String text = update.getMessage().getText();
                for (PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()) {
                    if (text.startsWith(paymentTypeEnum.getDisplayName())) {
                        PaymentConfig paymentConfig = paymentConfigService.getByPaymentType(paymentTypeEnum);
                        String message = paymentConfig.getOn() ? "выключен" : "включен";
                        paymentConfig.setOn(!paymentConfig.getOn());
                        paymentConfigService.save(paymentConfig);
                        responseSender.sendMessage(chatId, "Способ оплаты " +
                                paymentTypeEnum.getDisplayName() + " " + message + ".");
                        askForInput(chatId);
                        return;
                    }
                }
                break;
        }
    }

    private void askForInput(Long chatId) {
        List<ReplyButton> buttons = new ArrayList<>();
        for (PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()) {
            PaymentConfig paymentConfig = paymentConfigService.getByPaymentType(paymentTypeEnum);
            String isOn = paymentConfig != null && paymentConfig.getOn() ? "выключить" : "включить";
            buttons.add(ReplyButton.builder()
                    .text(paymentTypeEnum.getDisplayName() + "(" + isOn + ")")
                    .build());
        }
        buttons.add(ReplyButton.builder()
                .text(Command.CANCEL.getText())
                .build());

        responseSender.sendMessage(chatId, "Выберите тип оплаты чтобы выключить его или включить.",
                KeyboardUtil.buildReply(2, buttons));
    }
}
