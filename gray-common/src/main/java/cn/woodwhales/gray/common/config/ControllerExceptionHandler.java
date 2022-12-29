package cn.woodwhales.gray.common.config;

import cn.woodwhales.gray.common.model.RespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author woodwhales on 2022-12-29 9:26
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public RespVo<Void> handleException(Exception e) {
        log.error("Exception, {}", e.getMessage(), e);
        return RespVo.error(e.getMessage());
    }

}
