package cn.woodwhales.gray.common.config;

import cn.woodwhales.gray.common.constant.Constant;
import cn.woodwhales.gray.common.util.RequestTool;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * @author woodwhales on 2022-12-29 17:05
 */
@Slf4j
@Configuration
@ConditionalOnClass(Feign.class)
public class MyFeignRequestInterceptor implements RequestInterceptor {

    @Value("${config.serverName}")
    private String serverName;

    @Override
    public void apply(RequestTemplate template) {
        String headerValue = RequestTool.getHeader(Constant.GRAY_TAG_HEADER);
        log.info("{} ==> feign request with header => {}:{}", serverName, Constant.GRAY_TAG_HEADER, headerValue);
        template.header(Constant.GRAY_TAG_HEADER, headerValue);
    }
}
