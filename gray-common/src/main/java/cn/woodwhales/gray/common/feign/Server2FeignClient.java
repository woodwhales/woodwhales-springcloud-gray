package cn.woodwhales.gray.common.feign;

import cn.woodwhales.gray.common.model.ParamBody;
import cn.woodwhales.gray.common.model.RespVo;
import cn.woodwhales.gray.common.model.dto.ServerChain;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author woodwhales on 2022-12-28 20:31
 */
@FeignClient(name = "gray-server2")
public interface Server2FeignClient {

    @PostMapping("/save")
    RespVo<Object> save(@RequestBody ParamBody<ServerChain> param);

}
