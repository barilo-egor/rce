package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.bean.ApiUser;
import tgb.btc.rce.service.impl.ApiUserService;

import static tgb.btc.rce.web.controller.MainWebController.DEFAULT_MAPPER;

@Controller
@RequestMapping("/web/api/user")
public class ApiUserController {

    private ApiUserService apiUserService;

    @Autowired
    public void setApiUserService(ApiUserService apiUserService) {
        this.apiUserService = apiUserService;
    }

    @PostMapping("/create")
    @ResponseBody
    public ApiUser create(@RequestBody ApiUser apiUser) {
        return apiUserService.register(apiUser);
    }

    @GetMapping("/isExistById")
    @ResponseBody
    public ObjectNode isExistById(@RequestParam String id) {
        ObjectNode result = DEFAULT_MAPPER.createObjectNode();
        result.put("result", apiUserService.isExistsById(id));
        return result;
    }
}
