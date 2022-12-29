package cn.woodwhales.gray.common.util;

import cn.hutool.core.lang.UUID;
import cn.woodwhales.gray.common.constant.Constant;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author woodwhales on 2022-12-29 9:22
 */
@Slf4j
public class ControllerAopTool {

    public static Object providerAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Thread.currentThread().setName(UUID.randomUUID().toString(true));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Signature signature = joinPoint.getSignature();
        String declaringTypeName = signature.getDeclaringType()
                .getName();
        String fullMethodName = declaringTypeName + "#" + signature.getName();
        StringBuilder requestParamBuilder = new StringBuilder();
        String clientIP;
        String requestUrl = null;
        HttpServletRequest request;
        try {
            log.info("======================= {} start =======================", fullMethodName);
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            clientIP = getClientAddress(request);
            String methodType = request.getMethod();
            requestUrl = String.format("[ %s ] %s", methodType, request.getRequestURL()
                    .toString());
            // 请求ip、请求地址、请求参数
            log.info("request_ip => {}, request_url => {}, request_param => {}", clientIP, requestUrl, requestParamBuilder.append(getParamStringFromRequest(request)));
            if (!StringUtils.equals(RequestMethod.GET.name(), methodType)) {
                Object requestBodyParam = collectRequestBodyParam(joinPoint.getArgs(), signature);
                if (null != requestBodyParam) {
                    log.info("request_requestBody => {}", requestParamBuilder.append(JSON.toJSONString(requestBodyParam)));
                }
            }
            // 请求头信息
            log.info("request_header => {}={}", Constant.GRAY_TAG_HEADER, request.getHeader(Constant.GRAY_TAG_HEADER));
            request.setAttribute("traceId", Thread.currentThread().getName());
            Object result = joinPoint.proceed();
            printResponseBody(result);
            return result;
        } catch (Throwable throwable) {
            String requestParam = requestParamBuilder.toString();
            log.error("system happen error {}, requestParam : {}", requestUrl, requestParam, throwable);
            throw throwable;
        } finally {
            long costTime = stopWatch.getTotalTimeMillis();
            log.info("requestUrl = {}, consume : {} ms", requestUrl, costTime);
            log.info("======================= {} end =======================", fullMethodName);
        }
    }

    private static void printResponseBody(Object result) {
        try {
            if (null != result) {
                if(result instanceof Mono) {
                    (((Mono)result)).subscribe(resp -> {
                        if(resp instanceof ResponseEntity) {
                            ResponseEntity responseEntity = (ResponseEntity) resp;
                            HttpHeaders headers = responseEntity.getHeaders();
                            MediaType contentType = headers.getContentType();
                            if(Objects.nonNull(contentType) && MediaType.APPLICATION_OCTET_STREAM.getType().equals(contentType.getType())) {
                                log.info("request_responseBody={}", "文件流不打印响应日志");
                            } else {
                                log.info("request_responseBody={}", JSON.toJSONString(responseEntity.getBody()));
                            }
                        }
                    });
                } else {
                    log.info("request_responseBody={}", JSON.toJSONString(result));
                }
            }
        } catch (Exception e) {
            log.error("打印响应报文失败, {}", e.getMessage(), e);
        }
    }

    private static Object collectRequestBodyParam(Object[] args, Signature signature) {
        Method method = ((MethodSignature)signature).getMethod();
        Parameter[] parameters = method.getParameters();
        return Arrays.stream(parameters)
                .filter(parameter -> null != parameter.getAnnotation(RequestBody.class))
                .map(parameter -> ArrayUtils.indexOf(parameters, parameter))
                .map(index -> args[index])
                .findFirst()
                .orElse(null);
    }

    private static String getParamStringFromRequest(final HttpServletRequest request) {
        return parseEnumerationToString(request.getParameterNames(), request::getParameter);
    }

    private static String getHeaderStringFromRequest(final HttpServletRequest request) {
        return parseEnumerationToString(request.getHeaderNames(), request::getHeader);
    }

    private static String parseEnumerationToString(Enumeration<String> enumerations, Function<String, String> function) {
        List<String> list = ListUtils.emptyIfNull(EnumerationUtils.toList(enumerations));
        Map<String, String> map = list.stream()
                .collect(Collectors.toMap(Function.identity(), function));
        return Joiner.on("&")
                .withKeyValueSeparator("=")
                .join(map);
    }

    public static String getClientAddress(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            //根据网卡取本机配置的IP
            InetAddress inet = null;
            try {
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ip = inet.getHostAddress();
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        //"***.***.***.***".length() = 15
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
