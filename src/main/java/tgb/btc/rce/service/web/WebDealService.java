package tgb.btc.rce.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.repository.bot.paging.PagingDealRepository;
import tgb.btc.rce.vo.DealVO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebDealService {

    @Autowired
    private DealRepository dealRepository;

    private PagingDealRepository pagingDealRepository;

    @Autowired
    public void setPagingDealRepository(PagingDealRepository pagingDealRepository) {
        this.pagingDealRepository = pagingDealRepository;
    }

    public List<DealVO> findAll() {
        return dealRepository.findAll().stream()
                .map(deal -> DealVO.builder()
                        .pid(deal.getPid())
                        .dealStatus(deal.getDealStatus())
                        .chatId(dealRepository.getUserChatIdByDealPid(deal.getPid()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<DealVO> findAll(Integer page, Integer limit, Integer start) {
        return pagingDealRepository.findAll(PageRequest.of(page - 1, limit, Sort.by(Sort.Order.desc("pid")))).stream()
                .map(deal -> DealVO.builder()
                        .pid(deal.getPid())
                        .dealStatus(deal.getDealStatus())
                        .chatId(dealRepository.getUserChatIdByDealPid(deal.getPid()))
                        .build())
                .collect(Collectors.toList());
    }
}
