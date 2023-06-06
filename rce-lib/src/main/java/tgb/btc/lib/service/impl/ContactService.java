package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.lib.bean.Contact;
import tgb.btc.lib.repository.BaseRepository;
import tgb.btc.lib.repository.ContactsRepository;

import java.util.List;

@Service
public class ContactService extends BasePersistService<Contact> {
    private final ContactsRepository contactsRepository;

    @Autowired
    public ContactService(BaseRepository<Contact> baseRepository, ContactsRepository contactsRepository) {
        super(baseRepository);
        this.contactsRepository = contactsRepository;
    }

    public List<Contact> findAll() {
        return contactsRepository.findAll();
    }
}

