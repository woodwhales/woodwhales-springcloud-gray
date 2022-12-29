package cn.woodwhales.gray.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author woodwhales on 2022-12-29 20:02
 */
@Component
public class GrayRuleConfig {

    @Bean
    public GrayRule grayRule() {
        return new GrayRule();
    }

}
