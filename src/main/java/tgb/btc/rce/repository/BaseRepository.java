package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.BasePersist;

@Repository
public interface BaseRepository<T extends BasePersist> extends JpaRepository<T, Long> {
}

