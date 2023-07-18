package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.ApiUser;

@Repository
@Transactional
public interface ApiUserRepository extends BaseRepository<ApiUser> {

    @Query("select count(pid) from ApiUser where token=:token")
    long countByToken(String token);

    @Query("from ApiUser where token=:token")
    ApiUser getByToken(String token);

    @Query("select count(pid) from ApiUser where id=:id")
    long countById(String id);
}
