package cn.woodwhales.gray.common.feign;

import cn.woodwhales.gray.common.model.ParamBody;
import cn.woodwhales.gray.common.model.RespVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author woodwhales on 2022-12-28 20:31
 */
@FeignClient(name = "gray-server2")
public interface Server2FeignClient {

    @PostMapping("/save")
    RespVo<Map<String, Object>> save(@RequestBody ParamBody<String> paramBody);

}
