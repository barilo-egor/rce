package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.Contact;
import tgb.btc.library.interfaces.service.bean.bot.IContactService;

import java.net.MalformedURLException;
import java.net.URI;

@Service
public class EditContactsService {

    private IContactService contactService;

    @Autowired
    public void setContactService(IContactService contactService) {
        this.contactService = contactService;
    }

    public void save(String message) throws MalformedURLException {
        String[] contactData = message.split("\n");
        URI uri = URI.create(contactData[1]);
        contactService.save(Contact.builder().label(contactData[0]).url(uri.toURL().toExternalForm()).build());
    }
}
