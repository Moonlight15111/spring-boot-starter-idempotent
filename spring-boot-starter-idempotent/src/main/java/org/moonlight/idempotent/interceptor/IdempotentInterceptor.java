package org.moonlight.idempotent.interceptor;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.moonlight.idempotent.annotation.Idempotent;
import org.moonlight.idempotent.common.Constant;
import org.moonlight.idempotent.exception.ServiceException;
import org.moonlight.idempotent.handler.RedisHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Moonlight
 */
@Component
public class IdempotentInterceptor implements HandlerInterceptor {

    private final RedisHandler redisHandler;

    public IdempotentInterceptor(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println(" IdempotentInterceptor preHandle ");

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        if (idempotent != null) {
            String cipherText = generateCipherText(request), redisKey = Constant.HEADER_TOKEN_NAME + "_" + cipherText;
            if (redisHandler.exists(redisKey)) {
                throw new ServiceException("请不要重复提交");
            } else {
                redisHandler.setExpireTimeMilliseconds(redisKey, cipherText, idempotent.expiredTime());
                request.setAttribute(Constant.HEADER_TOKEN_NAME, redisKey);
                System.out.println("redis插入key " + redisKey + " 成功");
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println(" IdempotentInterceptor afterCompletion ");
        Object obj = request.getAttribute(Constant.HEADER_TOKEN_NAME);
        if (obj != null) {
            String key = obj.toString();
            if (StringUtils.isNotBlank(key) && redisHandler.del(key)) {
                System.out.println(" IdempotentInterceptor afterCompletion delete key " + key + " successful ");
            }
        }
    }

    /**
     * 计算密文，目前只是单纯的根据请求体的内容来计算密文
     * 后续的扩展可以考虑{@link Idempotent}中的说明
     *
     * @param request request请求对象
     * @return String cipherText 通过请求数据计算出来的密文
     * @author moonlight
     **/
    private String generateCipherText(HttpServletRequest request) {
        String body;
        try (ServletInputStream in = request.getInputStream()) {
            body = new String(IOUtils.toByteArray(in));
            // 这里需要必须加一个reset，因为IOUtils.toByteArray会将ServletInputStream的pos推到流的尾部，后面再读的时候，就会判定为流已经读取完毕
            // 从而导致无法从ServletInputStream流中获取到任何接口需要的参数
            in.reset();
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        return DigestUtils.sha1Hex(body);
    }
}