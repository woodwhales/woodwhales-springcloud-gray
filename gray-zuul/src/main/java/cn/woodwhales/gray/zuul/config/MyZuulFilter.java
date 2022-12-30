package cn.woodwhales.gray.zuul.config;

import cn.woodwhales.gray.common.constant.Constant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @author woodwhales on 2022-12-29 8:39
 */
@Slf4j
@Component
public class MyZuulFilter extends ZuulFilter {
    /**
     * pre 前缀类型过滤器
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String grayTag = ctx.getRequest().getHeader(Constant.GRAY_TAG_HEADER);
//        HttpServletRequest request = ctx.getRequest();
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()){
//            String headerName = (String) headerNames.nextElement();
//            log.info("header -> {} : {}", headerName, request.getHeader(headerName));
//        }
        log.info("header -> {} : {}", Constant.GRAY_TAG_HEADER, grayTag);
        return null;
    }
}
