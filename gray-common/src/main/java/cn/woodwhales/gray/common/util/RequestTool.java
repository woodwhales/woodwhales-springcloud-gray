package cn.woodwhales.gray.common.util;

import cn.woodwhales.gray.common.config.gateway.GlobalContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author woodwhales on 2022-12-29 17:10
 */
public class RequestTool {
    public static String getHeader(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(Objects.nonNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if(Objects.isNull(headerNames)) {
                return null;
            }
            while (headerNames.hasMoreElements()) {
                String requestHeaderName = headerNames.nextElement();
                if(StringUtils.equals(requestHeaderName, headerName)) {
                    return request.getHeader(requestHeaderName);
                }
            }
        } else {
            return GlobalContext.getCurrentEnvironment();
        }
        return null;
    }

}
