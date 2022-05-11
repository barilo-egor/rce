package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Contact;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.ContactsRepository;
import tgb.btc.rce.service.IContactService;

import java.util.List;

@Service
public class ContactService extends BasePersistService<Contact> implements IContactService {
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

