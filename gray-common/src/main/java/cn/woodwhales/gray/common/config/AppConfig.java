package cn.woodwhales.gray.common.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author woodwhales on 2022-12-29 16:41
 */
@Component
public class AppConfig {

    @Bean
    public SpringUtil springUtil() {
        return new SpringUtil();
    }
}
