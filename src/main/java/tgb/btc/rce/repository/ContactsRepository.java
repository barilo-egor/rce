package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Contact;

@Repository
@Transactional
public interface ContactsRepository extends BaseRepository<Contact> {
}

