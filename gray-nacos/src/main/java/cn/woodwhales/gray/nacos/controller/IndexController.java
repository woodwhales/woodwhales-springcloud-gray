package cn.woodwhales.gray.nacos.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;

/**
 * @author woodwhales on 2022-12-28 20:09
 */
@Slf4j
@RequestMapping
@RestController
// 必须添加此注解，否则 nacos 配置发布成功，当前程序配置更新不生效
@RefreshScope
public class IndexController {

    @Value("${config.grayTag}")
    private String grayTag;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @GetMapping("config")
    public String config() {
        log.info("config.grayTag = {}", configurableApplicationContext.getEnvironment().getProperty("config.grayTag"));
        return grayTag;
    }

    @PostMapping("/save")
    public Object save(@RequestBody Object param) {
        return param;
    }

}
