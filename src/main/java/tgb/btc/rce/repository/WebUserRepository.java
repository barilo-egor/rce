package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.WebUser;

@Repository
public interface WebUserRepository extends BaseRepository<WebUser> {

    @Query("select count(pid) from WebUser where username=:username")
    int countByUsername(String username);
}
