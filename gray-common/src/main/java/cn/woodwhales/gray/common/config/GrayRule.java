package cn.woodwhales.gray.common.config;

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
