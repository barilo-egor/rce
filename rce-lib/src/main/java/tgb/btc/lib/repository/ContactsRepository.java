package tgb.btc.lib.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.lib.bean.Contact;

@Repository
@Transactional
public interface ContactsRepository extends BaseRepository<Contact> {
}

