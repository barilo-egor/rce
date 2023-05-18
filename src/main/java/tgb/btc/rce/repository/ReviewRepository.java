package tgb.btc.rce.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Review;

import java.util.List;

@Repository
@Transactional
public interface ReviewRepository extends BaseRepository<Review> {

    List<Review> findAllByIsPublished(Boolean isPublished);
}
