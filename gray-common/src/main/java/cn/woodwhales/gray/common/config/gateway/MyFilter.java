package cn.woodwhales.gray.common.config.gateway;

import cn.woodwhales.gray.common.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author woodwhales on 2023-01-04 0:24
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class MyFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        GlobalContext.setCurrentEnvironment(request.getHeaders().get(Constant.GRAY_TAG_HEADER).get(0));
        return chain.filter(exchange.mutate().request(request).build());
    }
}
