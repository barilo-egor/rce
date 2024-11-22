package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tgb.btc.rce.constants.BotStringConstants.CALLBACK_DATA_SPLITTER;

@Service
public class EditContactsService {

    private ResponseSender responseSender;

    private IContactService contactService;

    private IKeyboardBuildService keyboardBuildService;

    private IMessagePropertiesService messagePropertiesService;
    
    private IUpdateService updateService;

    private ICallbackDataService callbackDataService;

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setContactService(IContactService contactService) {
        this.contactService = contactService;
    }

    public void save(String message) throws MalformedURLException {
        String[] contactData = message.split("\n");
        new URL(contactData[1]);
        contactService.save(Contact.builder().label(contactData[0]).url(contactData[1]).build());
    }

    public void askForChoose(Update update) {
        Long chatId = updateService.getChatId(update);
        Optional<Message> optionalMessage =
                responseSender.sendMessage(chatId, "Выберите контакт для удаления.");
        if (optionalMessage.isEmpty()) throw new BaseException("Не получено отправленное сообщение");
        Integer messageId = optionalMessage.get().getMessageId();
        responseSender.sendEditedMessageText(chatId, messageId,
                messagePropertiesService.getMessage(PropertiesMessage.CONTACT_ASK_DELETE),
                keyboardBuildService.buildInline(buildContactButtons(messageId)));
    }

    private List<InlineButton> buildContactButtons(Integer messageId) {
        return contactService.findAll().stream()
                .map(c -> InlineButton.builder()
                        .text(c.getLabel())
                        .data(callbackDataService.buildData(CallbackQueryData.DELETE_CONTACT, c.getPid(), messageId))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public void delete(Update update) {
        contactService.deleteById(Long.valueOf(update.getCallbackQuery().getData().split(CALLBACK_DATA_SPLITTER)[1]));
        responseSender.deleteMessage(update.getCallbackQuery().getFrom().getId(),
                Integer.valueOf(update.getCallbackQuery().getData().split(CALLBACK_DATA_SPLITTER)[2]));
        responseSender.sendMessage(update.getCallbackQuery().getFrom().getId(), "Контакт удален.");
    }
}
