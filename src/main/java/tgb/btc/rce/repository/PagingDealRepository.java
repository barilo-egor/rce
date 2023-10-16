package tgb.btc.rce.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Deal;

@Repository
@Transactional
public interface PagingDealRepository extends PagingAndSortingRepository<Deal, Long> {
}
