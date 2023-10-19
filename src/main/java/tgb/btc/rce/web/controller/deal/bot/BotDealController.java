package tgb.btc.rce.web.controller.deal.bot;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.util.web.JacksonUtil;
import tgb.btc.rce.constants.mapper.DealMapper;
import tgb.btc.rce.service.web.WebDealService;
import tgb.btc.rce.web.constant.ControllerMapping;

@RestController
@RequestMapping(ControllerMapping.BOT_DEAL)
public class BotDealController {

    private WebDealService webDealService;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setWebDealService(WebDealService webDealService) {
        this.webDealService = webDealService;
    }

    @GetMapping("/findAll")
    public ObjectNode findAll(Integer page, Integer limit, Integer start) {
        return JacksonUtil.pagingData(webDealService.findAll(page, limit, start), dealRepository.count(), DealMapper.FIND_ALL);
    }
}
