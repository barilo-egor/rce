package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.repository.web.ApiUserRepository;
import tgb.btc.library.service.bean.web.ApiUserService;
import tgb.btc.library.util.web.JacksonUtil;
import tgb.btc.rce.service.impl.ApiUserProcessService;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.web.vo.ApiUserVO;
import tgb.btc.web.constant.ControllerMapping;
import tgb.btc.web.util.SuccessResponseUtil;
import tgb.btc.web.vo.SuccessResponse;

import static tgb.btc.rce.web.controller.MainWebController.DEFAULT_MAPPER;

@Controller
@RequestMapping(ControllerMapping.API_USER)
public class ApiUserController {

    private ApiUserService apiUserService;

    private ApiUserRepository apiUserRepository;

    private ApiUserProcessService apiUserProcessService;

    @Autowired
    public void setApiUserProcessService(ApiUserProcessService apiUserProcessService) {
        this.apiUserProcessService = apiUserProcessService;
    }

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    @Autowired
    public void setApiUserService(ApiUserService apiUserService) {
        this.apiUserService = apiUserService;
    }

    @PostMapping("/save")
    @ResponseBody
    public SuccessResponse<?> save(@RequestBody ApiUserVO apiUserVO) {
        return SuccessResponseUtil.data(apiUserProcessService.save(apiUserVO));
    }

    @PostMapping("/update")
    @ResponseBody
    public SuccessResponse<?> update(@RequestBody ApiUserVO apiUserVO) {
       return SuccessResponseUtil.data(apiUserProcessService.save(apiUserVO));
    }

    @GetMapping("/isExistById")
    @ResponseBody
    public ObjectNode isExistById(@RequestParam String id) {
        ObjectNode result = DEFAULT_MAPPER.createObjectNode();
        result.put("result", apiUserService.isExistsById(id));
        return result;
    }

    @GetMapping("/findAll")
    @ResponseBody
    public SuccessResponse<?> findAll() {
        return SuccessResponseUtil.data(apiUserRepository.findAll());
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public Boolean deleteUser(@RequestParam Long pid) {
        apiUserRepository.deleteById(pid);
        return true;
    }

    @GetMapping("/isOn")
    @ResponseBody
    public SuccessResponse<?> isOn(@RequestParam FiatCurrency fiatCurrency) {
        boolean result = FiatCurrencyUtil.getFiatCurrencies().contains(fiatCurrency);
        return SuccessResponseUtil.getDataObjectNode(JacksonUtil.getEmpty()
                .put("result", result));
    }
}
