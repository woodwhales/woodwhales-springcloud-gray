package cn.woodwhales.gray.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.extra.spring.SpringUtil;
import cn.woodwhales.gray.common.constant.Constant;
import cn.woodwhales.gray.common.model.dto.ServerInfo;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author woodwhales on 2022-12-29 16:34
 */
public class ServerInfoTool {

    public static ServerInfo build() {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        String serverName = environment.getProperty("config.serverName");
        Integer serverPort = environment.getProperty("server.port", Integer.class);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        String acceptHeader = request.getHeader(Constant.GRAY_TAG_HEADER);

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerName(serverName);
        serverInfo.setServerPort(serverPort);
        serverInfo.setAcceptHeader(acceptHeader);
        serverInfo.setDateTime(DateFormatUtils.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        return serverInfo;
    }

}
