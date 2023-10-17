package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.ApiUserRepository;
import tgb.btc.rce.service.impl.ApiUserProcessService;
import tgb.btc.rce.service.impl.bean.ApiUserService;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.util.JsonUtil;
import tgb.btc.rce.web.util.SuccessResponseUtil;
import tgb.btc.rce.web.vo.ApiUserVO;
import tgb.btc.rce.web.vo.SuccessResponse;

import static tgb.btc.rce.web.controller.MainWebController.DEFAULT_MAPPER;

@Controller
@RequestMapping("/web/api/user")
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
    public ObjectNode save(@RequestBody ApiUserVO apiUserVO) {
        return apiUserProcessService.save(apiUserVO).toJson();
    }

    @PostMapping("/update")
    @ResponseBody
    public ObjectNode update(@RequestBody ApiUserVO apiUserVO) {
       return apiUserProcessService.save(apiUserVO).toJson();
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
    public ArrayNode findAll() {
        return JsonUtil.toJsonArray(apiUserRepository.findAll());
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
