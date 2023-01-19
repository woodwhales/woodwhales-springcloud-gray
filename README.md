# woodwhales-springcloud-gray

Spring-Cloud 灰度发布服务端口说明

| 服务                | 端口号  |
|-------------------|------|
| gray-eureka       | 9079 |
| gray-zuul         | 9080 |
| gray-server1-new  | 9083 |
| gray-server1-old  | 9081 |
| gray-server2-new1 | 9084 |
| gray-server2-new2 | 9085 |
| gray-server2-old  | 9082 |
| gray-gateway      | 9086 |
| gray-nacos        | 9087 |

上述除 gray-eureka、gray-nacos 之外均需要引入自定义负载均衡策略：

步骤1：自定义 IRule 接口实现：cn.woodwhales.gray.common.config.GrayRule

```java
import cn.hutool.extra.spring.SpringUtil;
import cn.woodwhales.gray.common.constant.Constant;
import cn.woodwhales.gray.common.util.DataTool;
import cn.woodwhales.gray.common.util.RequestTool;
import com.google.common.base.Optional;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author woodwhales on 2022-12-29 19:49
 */
@Slf4j
public class GrayRule extends ZoneAvoidanceRule {

    @Override
    public Server choose(Object key) {
        String serverName = SpringUtil.getProperty("config.serverName");
        List<Server> servers = this.getLoadBalancer().getReachableServers();
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        String grayTag = RequestTool.getHeader(Constant.GRAY_TAG_HEADER);
        List<Server> normalServerList = new ArrayList<>();
        Map<String, List<Server>> map = new HashMap<>(16);
        for (Server server : servers) {
            Map<String, String> metadataMap = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
            String configGrayTag = metadataMap.get(Constant.GRAY_TAG_HEADER);
            if(StringUtils.isBlank(configGrayTag)) {
                normalServerList.add(server);
            } else {
                if(map.containsKey(configGrayTag)) {
                    map.get(configGrayTag).add(server);
                } else {
                    List<Server> serverList = new ArrayList<>();
                    serverList.add(server);
                    map.put(configGrayTag, serverList);
                }
            }
        }

        List<Server> selectServers;
        if(StringUtils.isBlank(grayTag)) {
            selectServers = normalServerList;
        } else {
            selectServers = map.get(grayTag);
        }
        log.info("grayTag => {} serverName {} => {}", grayTag, serverName, DataTool.toList(selectServers, Server::getHostPort));
        return originChoose(selectServers, key);
    }

    private Server originChoose(List<Server> serverList, Object key) {
        Optional<Server> server = getPredicate().chooseRoundRobinAfterFiltering(serverList, key);
        if (server.isPresent()) {
            return server.get();
        } else {
            return null;
        }
    }

}
```

步骤2：并将其注入到 spring 容器中

```java
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
```

步骤3：将自定义的 IRule 接口实现配置到 @RibbonClients 中

```java
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author woodwhales on 2022-12-29 16:41
 */
@RibbonClients(value = {
        @RibbonClient(value = "${spring.application.name}", configuration = GrayRuleConfig.class)
})
@Component
public class AppConfig {

    @Bean
    public SpringUtil springUtil() {
        return new SpringUtil();
    }
}
```

步骤4：自定义 feign 拦截器

```java
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
```

