package tgb.btc.lib.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.lib.bean.Review;

import java.util.List;

@Repository
@Transactional
public interface ReviewRepository extends BaseRepository<Review> {
    List<Review> findAllByIsPublished(Boolean isPublished);
}
