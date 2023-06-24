package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.Role;

@Repository
public interface RoleRepository extends BaseRepository<Role> {

    @Query("from Role where name=:name")
    Role getByName(String name);
}
