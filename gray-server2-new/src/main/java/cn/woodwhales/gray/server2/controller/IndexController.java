package cn.woodwhales.gray.server2.controller;

import cn.woodwhales.gray.common.feign.Server2FeignClient;
import cn.woodwhales.gray.common.model.ParamBody;
import cn.woodwhales.gray.common.model.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author woodwhales on 2022-12-28 20:09
 */
@Slf4j
@RequestMapping
@RestController
public class IndexController {

    @Value("${spring.application.name}")
    private String applicationName;

    @PostMapping("/save")
    public RespVo<Map<String, Object>> save(@RequestBody ParamBody<Void> paramBody) {
        log.info("param = {}", paramBody.toString());
        Map<String, Object> map = new HashMap<>();
        map.put("param", paramBody);
        map.put("applicationName", applicationName);
        return RespVo.success(map);
    }

}
