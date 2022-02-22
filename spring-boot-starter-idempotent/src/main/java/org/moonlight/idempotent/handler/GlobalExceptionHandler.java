package org.moonlight.idempotent.handler;

import lombok.extern.slf4j.Slf4j;
import org.moonlight.idempotent.vo.ResultVo;
import org.moonlight.idempotent.exception.CacheException;
import org.moonlight.idempotent.exception.ServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author Moonlight
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        ResultVo r = ResultVo.error();
        r.put("code", 400);
        StringBuffer msg = new StringBuffer();
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getAllErrors().forEach(item -> {
            msg.append(item.getDefaultMessage());
        });
        r.put("msg", msg);
        return r;
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResultVo handleHttpMessageNotWritableException(HttpMessageNotWritableException e) {
        log.error(e.getMessage(), e);
        ResultVo r = ResultVo.error();
        r.put("code", 400);
        r.put("msg", "数据传输错误");
        return r;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return ResultVo.error().put("code", 400).put("msg", "不支持'" + e.getMethod() + "'请求方法");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVo handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return ResultVo.error().put("code", 400).put("msg", e.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResultVo handleServiceException(ServiceException e) {
        log.error(e.getMessage(), e);
        return ResultVo.error().put("code", 400).put("msg", e.getMessage());
    }

    @ExceptionHandler(CacheException.class)
    public ResultVo handleServiceException(CacheException e) {
        log.error(e.getMessage(), e);
        return ResultVo.error().put("code", 400).put("msg", e.getMessage());
    }

}
