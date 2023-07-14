package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.bean.ApiUser;
import tgb.btc.rce.repository.ApiUserRepository;
import tgb.btc.rce.service.impl.ApiUserService;
import tgb.btc.rce.web.util.JsonUtil;

import static tgb.btc.rce.web.controller.MainWebController.DEFAULT_MAPPER;

@Controller
@RequestMapping("/web/api/user")
public class ApiUserController {

    private ApiUserService apiUserService;

    private ApiUserRepository apiUserRepository;

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
    public ObjectNode save(@RequestBody ApiUser apiUser) {
        return apiUserService.register(apiUser).toJson();
    }

    @PostMapping("/update")
    @ResponseBody
    public ObjectNode update(@RequestBody ApiUser apiUser) {
//        return apiUserRepository.save(apiUser).toJson();
        return null;
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
}
