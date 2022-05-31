package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.CryptoCurrency;

@Repository
@Transactional
public interface DealRepository extends BaseRepository<Deal> {

    @Modifying
    @Query("update Deal set cryptoCurrency=:cryptoCurrency where pid=:pid")
    void updateCryptoCurrencyByPid(@Param("pid") Long pid, @Param("cryptoCurrency") CryptoCurrency cryptoCurrency);

    @Query("select cryptoCurrency from Deal where pid=:pid")
    CryptoCurrency getCryptoCurrencyByPid(@Param("pid") Long pid);
}
