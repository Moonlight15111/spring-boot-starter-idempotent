package org.moonlight.idempotent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识方法需要进行幂等判断
 *
 * 目前来说是直接通过请求参数的整个body进行key的计算
 * 后续可以考虑指定使用请求参数的某些字段参与key的计算
 *            指定某些请求的请求头参与key的计算
 *            指定请求的方式参与key的计算
 *            指定计算key时使用的算法
 *
 * @author Moonlight
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /** 过期时间 单位毫秒 **/
    long expiredTime() default 60000L;

}