package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentConfig;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PaymentTypeEnum;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.PaymentConfigService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;

// TODO выкосить
@CommandProcessor(command = Command.PAYMENT_REQUISITES)
public class PaymentRequisites extends Processor {

    private PaymentConfigService paymentConfigService;

    @Autowired
    public void setPaymentConfigService(PaymentConfigService paymentConfigService) {
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
                userService.nextStep(chatId, Command.PAYMENT_REQUISITES);
                break;
            case 1:
                PaymentTypeEnum paymentTypeEnum = PaymentTypeEnum.fromDisplayName(update.getMessage().getText());
                userService.updateBufferVariable(chatId, paymentTypeEnum.name());
                responseSender.sendMessage(chatId, "Введите новые реквизиты.");
                userService.nextStep(chatId);
                return;
            case 2:
                String text = update.getMessage().getText();
                paymentTypeEnum = PaymentTypeEnum.valueOf(userService.getBufferVariable(chatId));
                PaymentConfig paymentConfig = paymentConfigService.getByPaymentType(paymentTypeEnum);
                paymentConfig.setRequisites(text);
                paymentConfigService.save(paymentConfig);
                responseSender.sendMessage(chatId, "Реквизиты " +
                        paymentTypeEnum.getDisplayName() + " заменены.");
                processToAdminMainPanel(chatId);
                break;
        }
    }

    private void askForInput(Long chatId) {
        List<ReplyButton> buttons = new ArrayList<>();
        for (PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()) {
            buttons.add(ReplyButton.builder()
                    .text(paymentTypeEnum.getDisplayName())
                    .build());
        }
        buttons.add(ReplyButton.builder()
                .text(Command.CANCEL.getText())
                .build());

        responseSender.sendMessage(chatId, "Выберите тип оплаты для замены реквизитов.",
                KeyboardUtil.buildReply(buttons));
    }
}
