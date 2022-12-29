package cn.woodwhales.gray.server1.controller;

import cn.woodwhales.gray.common.feign.Server2FeignClient;
import cn.woodwhales.gray.common.model.ParamBody;
import cn.woodwhales.gray.common.model.RespVo;
import cn.woodwhales.gray.common.model.dto.ServerChain;
import cn.woodwhales.gray.common.util.ParamBodyTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author woodwhales on 2022-12-28 20:09
 */
@Slf4j
@RequestMapping
@RestController
public class IndexController {

    @Autowired
    private Server2FeignClient feignClient;

    @PostMapping("/save")
    public RespVo<Object> save(@RequestBody ParamBody<ServerChain> param) {
        return feignClient.save(ParamBodyTool.build(param));
    }

}
