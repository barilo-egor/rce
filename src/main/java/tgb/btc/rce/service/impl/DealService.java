package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.Contact;
import tgb.btc.rce.repository.BaseRepository;

@Service
public class DealService extends BasePersistService<Contact> {

    @Autowired
    public DealService(BaseRepository<Contact> baseRepository) {
        super(baseRepository);
    }
}
